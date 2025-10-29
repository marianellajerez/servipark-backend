package com.servipark.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TipoVehiculoCreateDTO(
        @NotBlank(message = "{tipoVehiculo.validation.nombre.notBlank}")
        @Size(max = 50, message = "{tipoVehiculo.validation.nombre.size}")
        String nombre
) {
}