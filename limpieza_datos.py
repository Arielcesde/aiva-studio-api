import pandas as pd
import numpy as np


def cargar_datos(ruta):
    return pd.read_csv(ruta, encoding="utf-8-sig")


def inspeccionar_datos(df):
    print("=== INSPECCIÓN INICIAL DE AIVA STUDIO ===")
    print("PRIMERAS FILAS:")
    print(df.head())
    print("\nINFORMACIÓN GENERAL:")
    df.info()
    print("\nDuplicados exactos:", df.duplicated().sum())


def limpiar_datos(df):
    datos = df.copy()
    iniciales = len(datos)
    duplicados = int(datos.duplicated().sum())
    datos = datos.drop_duplicates().copy()

    # Normalización de textos
    textos = ["id_generacion", "fecha", "usuario", "proyecto",
              "modelo_ia", "estado_proyecto", "resolucion"]
    
    for columna in textos:
        datos[columna] = datos[columna].astype("string")
        datos[columna] = datos[columna].str.strip()
        datos[columna] = datos[columna].replace("", pd.NA)

    # Convertir a mayúsculas/formato estándar modelos y usuarios
    for columna in ["usuario", "proyecto", "modelo_ia", "estado_proyecto"]:
        datos[columna] = datos[columna].str.upper()

    # Parseo explícito de fechas
    fechas = pd.Series(pd.NaT, index=datos.index, dtype="datetime64[ns]")
    formatos = ["%Y-%m-%d", "%d/%m/%Y", "%Y/%m/%d", "%d-%m-%Y"]
    for formato in formatos:
        intento = pd.to_datetime(datos["fecha"], format=formato, errors="coerce")
        fechas = fechas.fillna(intento)
    datos["fecha"] = fechas

    # Parseo numérico
    for columna in ["duracion_seg", "creditos_consumidos"]:
        datos[columna] = pd.to_numeric(datos[columna], errors="coerce")
        datos[columna] = datos[columna].replace([np.inf, -np.inf], np.nan)

    # Auditoría de calidad / Reglas de negocio
    motivo = pd.Series("", index=datos.index)
    reglas = {
        "Fecha inválida; ": datos["fecha"].isna(),
        "Duración inválida (<=0); ": (
            datos["duracion_seg"].isna() | (datos["duracion_seg"] <= 0)
        ),
        "Créditos inválidos (<=0); ": (
            datos["creditos_consumidos"].isna() | (datos["creditos_consumidos"] <= 0)
        )
    }

    for mensaje, condicion in reglas.items():
        motivo.loc[condicion] += mensaje

    rechazadas = datos.loc[motivo != ""].copy()
    rechazadas["motivo"] = motivo.loc[motivo != ""]
    datos = datos.loc[motivo == ""].copy()

    # Imputación prudente para metadata descriptiva
    for columna in ["usuario", "proyecto", "estado_proyecto"]:
        datos[columna] = datos[columna].fillna("SIN_REGISTRAR")

    # Métrica calculada: Costo por segundo de renderizado
    datos["costo_por_segundo"] = (
        datos["creditos_consumidos"] / datos["duracion_seg"]
    )

    calidad = {
        "Generaciones recibidas": iniciales,
        "Duplicados eliminados": duplicados,
        "Filas a revisión técnica": len(rechazadas),
        "Generaciones analizadas": len(datos)
    }

    return datos, rechazadas, calidad


def guardar_avances(datos, rechazadas, carpeta):
    carpeta.mkdir(parents=True, exist_ok=True)
    datos.to_csv(carpeta / "datos_v1.csv", index=False,
                 encoding="utf-8-sig", date_format="%Y-%m-%d")
    rechazadas.to_csv(carpeta / "filas_por_revisar.csv",
                     index=False, encoding="utf-8-sig")