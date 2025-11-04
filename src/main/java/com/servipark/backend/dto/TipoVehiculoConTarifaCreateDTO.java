package com.servipark.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TipoVehiculoConTarifaCreateDTO(
        @NotBlank(message = "{tipoVehiculo.validation.nombre.notBlank}")
        @Size(max = 100, message = "{tipoVehiculo.validation.nombre.size}")
        String nombre,

        @NotNull(message = "{tarifa.validation.valorPorMinuto.notBlank}")
        @Positive(message = "{tarifa.validation.valorPorMinuto.positive}")
        Double valorPorMinuto
) {
}
