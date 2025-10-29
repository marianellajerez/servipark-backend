package com.servipark.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDTO(
        @NotBlank(message = "{usuario.validation.nombre.notBlank}")
        String nombre,

        @NotBlank(message = "{usuario.validation.correo.notBlank}")
        @Email(message = "{usuario.validation.correo.email}")
        String correo,

        @NotBlank(message = "{usuario.validation.contrasena.notBlank}")
        @Size(min = 8, message = "{usuario.validation.contrasena.size}")
        String contrasena,

        @NotNull(message = "{usuario.validation.idRol.notNull}")
        Long idRol
) {
}