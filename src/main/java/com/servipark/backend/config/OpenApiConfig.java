package com.servipark.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils; // <-- 1. IMPORTAR
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Principal; // <-- 2. IMPORTAR

@Configuration
public class OpenApiConfig {

    // --- 3. AÑADIR ESTE BLOQUE ESTÁTICO ---
    static {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(Principal.class);
    }
    // ------------------------------------

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .description("Token JWT de autenticación. Ingresar 'Bearer [token]'")
                        )
                );
    }
}