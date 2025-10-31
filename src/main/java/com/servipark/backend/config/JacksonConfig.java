package com.servipark.backend.config;

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    // Define el formato UTC que se requiere (con la 'Z')
    // El patrón 'yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'' maneja hasta microsegundos
    // Si tu precisión es de nanosegundos (9 decimales), usa SSSSSSSSS
    // Vamos a usar uno que se adapta:
    private static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern(UTC_FORMAT);

            LocalDateTimeSerializer localDateTimeSerializer = new LocalDateTimeSerializer(utcFormatter);

            builder.serializerByType(java.time.LocalDateTime.class, localDateTimeSerializer);
        };
    }
}