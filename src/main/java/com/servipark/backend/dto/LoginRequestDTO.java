package com.servipark.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "{usuario.validation.correo.notBlank}")
        @Email(message = "{usuario.validation.correo.email}")
        String correo,

        @NotBlank(message = "{usuario.validation.contrasena.notBlank}")
        String contrasena
) {
}