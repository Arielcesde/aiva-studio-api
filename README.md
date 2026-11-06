# Aiva Studio API

# EQUIPO DE TRABAJO 
- Ariel De la Rosa Reales
- Gendris Urbina Urbina
- Victoria Belen 
- Yeison Arbey Garcia Mesa

API REST de **Aiva Studio**, una plataforma para crear videos con modelos de inteligencia artificial.
Construida con Spring Boot, PostgreSQL y autenticación por token Bearer.

- **Repositorio:** https://github.com/urbinagendriscmyk/aiva-studio-api
- **API desplegada (Render):** `https://<tu-servicio>.onrender.com`  <!-- reemplaza por la URL real -->

## Documentación interactiva

| Herramienta | Local | Desplegada |
| --- | --- | --- |
| Swagger UI | http://localhost:8080/swagger-ui.html | `https://<tu-servicio>.onrender.com/swagger-ui.html` |
| Scalar | http://localhost:8080/scalar | `https://<tu-servicio>.onrender.com/scalar` |
| Contrato OpenAPI (JSON) | http://localhost:8080/v3/api-docs | `https://<tu-servicio>.onrender.com/v3/api-docs` |

Las tres rutas son públicas. Para probar endpoints protegidos, inicia sesión con `POST /api/sessions`,
copia el token y pégalo en el botón **Authorize** de Swagger UI (o en la sección de autenticación de Scalar).

## Idea de negocio

Aiva Studio permite a creadores de contenido generar videos a partir de una descripción de texto
(*prompt*), eligiendo entre varios modelos de IA con distinto costo y estilo. El negocio funciona con un
sistema de **créditos**:

1. El usuario se registra y recibe **100 créditos iniciales**.
2. Elige un modelo de IA del catálogo; cada modelo tiene un costo en créditos por segundo.
3. Crea un proyecto de video indicando prompt, duración, formato de pantalla y estilo visual.
4. El sistema calcula el costo (`costo por segundo × duración`) y lo descuenta del saldo.
5. Cuando se acaban los créditos, el usuario recarga su saldo y las recargas grandes reciben un bono.

### Reglas de negocio implementadas

- **Créditos suficientes:** no se crea un proyecto si el saldo no cubre su costo.
- **Costo calculado en el servidor:** el cliente nunca envía el costo; se calcula con el modelo y la duración.
- **Duraciones permitidas:** 5, 10, 15, 30, 45 o 60 segundos.
- **Recarga mínima:** 50 créditos.
- **Bono por recarga:** desde 500 créditos se acredita un bono adicional del 20 %.
- **Correo único:** no pueden existir dos cuentas con el mismo correo.
- **Modelos activos:** solo se pueden usar modelos de IA activos del catálogo.
- **Aislamiento por usuario:** cada usuario solo consulta y opera sobre su propia cuenta.
- **Saldo máximo:** se rechaza una recarga que desborde el saldo permitido.

## Equipo

| Integrante | GitHub | Rol |
| --- | --- | --- |
| Gendris Urbina | [@urbinagendris-cmyk](https://github.com/urbinagendris-cmyk) | _completar_ |
| Yeison García | [@ygarciaar](https://github.com/ygarciaar) | _completar_ |
| Ariel | [@Arielcesde](https://github.com/Arielcesde) | _completar_ |

## Tecnologías

| Tecnología | Uso |
| --- | --- |
| Java 17 | Lenguaje |
| Spring Boot 4.1 (Web MVC) | Framework de la API REST |
| Spring Data JPA / Hibernate | Persistencia y mapeo objeto-relacional |
| PostgreSQL (Neon en producción) | Base de datos |
| Spring Security | Autenticación stateless con token Bearer |
| Bean Validation | Validación declarativa de los datos de entrada |
| springdoc-openapi | Generación del contrato OpenAPI, Swagger UI y Scalar |
| Docker / Docker Compose | Contenedorización y ejecución local |
| Render | Despliegue |
| Maven | Construcción y dependencias |

## Arquitectura

Arquitectura en capas bajo el paquete `com.aivastudio`:

```
config/       Seguridad, CORS, OpenAPI, carga inicial de datos y logging de peticiones
controller/   Capa web: recibe la petición, invoca al servicio y responde
service/      Lógica y reglas de negocio, transacciones
repository/   Acceso a datos (Spring Data JPA)
model/
  base/       BaseEntity (id y auditoría)
  entity/     Entidades JPA
  embeddable/ Objetos incrustados
  enums/      Enumerados del dominio
  dto/        Objetos de entrada (Request) y salida (Response)
exception/    Excepciones de negocio y manejo global de errores
```

### Modelo de dominio

| Entidad | Tabla | Descripción |
| --- | --- | --- |
| `User` | `users` | Cuenta del usuario, saldo de créditos y rol |
| `Subscription` | `subscriptions` | Plan y estado de la suscripción del usuario |
| `AiModel` | `ai_models` | Catálogo de modelos de IA con su costo por segundo |
| `Project` | `projects` | Proyecto de video generado por un usuario con un modelo |
| `Tag` | `tags` | Etiquetas para clasificar proyectos |
| `Credit` | `credits` | Movimientos de créditos (recargas y consumos) |
| `AuthToken` | `auth_tokens` | Tokens de sesión emitidos y su vigencia |

Relaciones:

| Tipo | Relación |
| --- | --- |
| `@OneToOne` | `User` ↔ `Subscription` |
| `@OneToMany` / `@ManyToOne` | `User` → `Project`, `AiModel` → `Project`, `User` → `Credit`, `User` → `AuthToken` |
| `@ManyToMany` | `Project` ↔ `Tag` (tabla intermedia `project_tags`) |

Otros elementos del modelo:

- **`BaseEntity`** (`@MappedSuperclass`): `id` autogenerado con `IDENTITY` y fechas de auditoría
  (`fechaCreacion`, `fechaActualizacion`) mediante `@EnableJpaAuditing`. Las entidades heredan de ella.
- **`ConfiguracionVideo`** (`@Embeddable`): estilo visual, formato de pantalla, resolución y duración,
  incrustada en `Project` con `@Embedded`.
- **Enums** mapeados con `@Enumerated(EnumType.STRING)`: `EstadoProyecto`, `FormatoPantalla` y `RolUsuario`.

## Endpoints

Los endpoints de registro e inicio de sesión son públicos. Los demás requieren el encabezado
`Authorization: Bearer <token>`.

| Método | Ruta | Uso |
| --- | --- | --- |
| `POST` | `/api/users` | Registrar un usuario (`nombre`, `email`, `password`) |
| `GET` | `/api/users/{userId}` | Consultar el perfil y los créditos |
| `POST` | `/api/sessions` | Iniciar sesión (`email`, `password`) |
| `DELETE` | `/api/sessions/current` | Cerrar sesión (revoca el token actual) |
| `GET` | `/api/ai-models` | Listar los modelos de IA activos y su costo por segundo |
| `POST` | `/api/ai-models` | Registrar un modelo de IA |
| `POST` | `/api/projects` | Crear un proyecto de video |
| `GET` | `/api/users/{userId}/projects` | Listar proyectos del usuario (filtro opcional `?estado=`) |
| `GET` | `/api/users/{userId}/credits` | Consultar los movimientos de créditos |
| `GET` | `/api/users/{userId}/credits/total` | Total de créditos acreditados al usuario |
| `POST` | `/api/users/{userId}/credits` | Recargar créditos (`cantidad`) |

Estados de proyecto para el filtro: `EN_COLA`, `BORRADOR`, `EN_PROCESO`, `COMPLETADO`, `ARCHIVADO`.

### Formato de errores

Todos los errores se centralizan en `GlobalExceptionHandler` y responden con el mismo formato JSON:

```json
{
  "timestamp": "2026-11-05T19:53:40.830723",
  "status": 400,
  "error": "Bad Request",
  "message": "La duración mínima es de 5 segundos",
  "errors": { "duracionSeg": "La duración mínima es de 5 segundos" }
}
```

El campo `errors` solo aparece en errores de validación. Códigos usados: `400` (validación o regla de
negocio), `401` (credenciales o token inválidos), `403` (acceso denegado), `404` (recurso inexistente),
`409` (violación de integridad) y `500` (error inesperado).

## Variables de entorno

La aplicación no guarda datos sensibles en el código. Toda la configuración llega por variables de
entorno, definidas en un archivo `.env` local o en el panel del servicio de despliegue.

| Variable | Obligatoria | Descripción |
| --- | --- | --- |
| `DB_URL` | Sí | URL JDBC de PostgreSQL (en Neon, incluye `sslmode=require`) |
| `DB_USERNAME` | Sí | Usuario de la base de datos |
| `DB_PASSWORD` | Sí | Contraseña de la base de datos |
| `PORT` | No | Puerto del servidor. Por defecto `8080`; Render lo asigna automáticamente |
| `CORS_ORIGINS` | No | Orígenes permitidos separados por coma. Por defecto `http://localhost:5173` |

Si falta alguna variable obligatoria, la aplicación no arranca y muestra
`Could not resolve placeholder`.

## Ejecución local

**Requisitos:** Java 17 y una base de datos PostgreSQL accesible.

1. Copia la plantilla de variables y complétala con tus datos:

   ```bash
   cp .env.example .env
   ```

2. Inicia la aplicación:

   ```bash
   # macOS / Linux (si aparece "permission denied": chmod +x mvnw)
   ./mvnw spring-boot:run
   ```

   ```powershell
   # Windows
   .\mvnw.cmd spring-boot:run
   ```

3. Abre http://localhost:8080/swagger-ui.html o http://localhost:8080/scalar.

Al arrancar por primera vez, `DataSeederConfig` carga el catálogo inicial de modelos de IA (solo si la
tabla está vacía). Las tablas se crean automáticamente con `spring.jpa.hibernate.ddl-auto=update`.

## Ejecución con Docker

1. Copia `.env.example` como `.env` y define `DB_PASSWORD` con una contraseña local segura.
2. Inicia los servicios desde la raíz del proyecto:

   ```bash
   docker compose up --build
   ```

La API queda en http://localhost:8080 y PostgreSQL conserva sus datos en el volumen `postgres_data`.
Para permitir el origen del frontend, configura `CORS_ORIGINS` en `.env`, por ejemplo
`http://localhost:5173,http://localhost:4173`.

El `Dockerfile` usa una construcción multietapa: compila con Maven y Temurin JDK 17 y empaqueta solo el
JAR en una imagen ligera de Temurin JRE 17, que se ejecuta con un usuario sin privilegios. El
`.dockerignore` excluye `.git`, los archivos `.env`, `target` y los directorios del IDE.

## Despliegue en Render

1. Crea un servicio web en Render a partir del repositorio, con entorno **Docker**.
2. Define estas variables de entorno en el panel del servicio:
   - `DB_URL`: URL JDBC de Neon, incluyendo `sslmode=require`.
   - `DB_USERNAME`: usuario de la base de datos.
   - `DB_PASSWORD`: contraseña de la base de datos. Guárdala como variable secreta y nunca en Git.
   - `CORS_ORIGINS`: origen exacto del frontend publicado, por ejemplo `https://tu-frontend.onrender.com`.
   - `PORT`: lo proporciona Render automáticamente; la aplicación escucha en ese puerto.
3. Despliega y verifica `https://<tu-servicio>.onrender.com/swagger-ui.html`.

No uses las credenciales de Neon en el frontend: este debe comunicarse únicamente con la URL pública de
esta API.

## Pruebas con Postman

Importa `postman/Aiva-Studio-API.postman_collection.json` y, opcionalmente,
`postman/Local.postman_environment.json` (apunta a `http://localhost:8080`). Ejecuta la colección completa
con el backend activo.

La colección prueba el registro y el inicio de sesión, el catálogo de modelos, el perfil, la creación y
el listado de proyectos, los movimientos de créditos, la recarga y el cierre de sesión. Genera un correo
único `@example.test` en cada ejecución. Los datos se guardan en la base configurada: no la ejecutes
contra datos de producción si no quieres conservar esos registros.

## Registro de peticiones

El servidor imprime en consola cada solicitud REST con método, ruta, estado HTTP y duración. Por
seguridad, no registra cuerpos de solicitud, contraseñas, tokens ni valores enlazados a las consultas SQL.

## Seguridad

- Autenticación stateless con token Bearer; sesiones revocables con `DELETE /api/sessions/current`.
- Contraseñas almacenadas con BCrypt; nunca se devuelven en las respuestas.
- Las respuestas usan DTOs de salida que no exponen campos internos de las entidades.
- CORS limitado a los orígenes definidos en `CORS_ORIGINS`.
- Las credenciales viven solo en variables de entorno; `.env` está excluido de Git y de la imagen Docker.