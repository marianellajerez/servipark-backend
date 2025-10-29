package com.servipark.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TarifaCreateDTO(
        @NotNull(message = "{tarifa.validation.valorPorMinuto.notBlank}")
        @Positive(message = "{tarifa.validation.valorPorMinuto.positive}")
        Double valorPorMinuto,

        @NotNull(message = "{tarifa.validation.idTipoVehiculo.notBlank}")
        Long idTipoVehiculo
) {
}