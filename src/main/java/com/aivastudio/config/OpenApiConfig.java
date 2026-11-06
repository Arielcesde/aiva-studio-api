package com.aivastudio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI aivaStudioOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Aiva Studio API")
                        .version("1.0.0")
                        .description("""
                                API REST de Aiva Studio, una plataforma para crear videos con modelos de \
                                inteligencia artificial. Los usuarios se registran, reciben créditos iniciales, \
                                eligen un modelo de IA y generan proyectos de video; cada proyecto consume \
                                créditos según el costo por segundo del modelo y la duración elegida. \
                                Las recargas de 500 créditos o más reciben un bono del 20%.""")
                        .contact(new Contact()
                                .name("Equipo, Victortia, Ariel, Gendris,y Yeison")
                                .email("NA")))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .description("Token obtenido en POST /api/sessions o POST /api/users")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
