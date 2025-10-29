package com.servipark.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TarifaRequestDTO(
        @NotBlank(message = "{tarifa.validation.valorPorMinuto.notBlank}")
        @Positive(message = "{tarifa.validation.valorPorMinuto.positive}")
        Double valorPorMinuto,

        @NotBlank(message = "{tarifa.validation.idTipoVehiculo.notBlank}")
        Long idTipoVehiculo
) {
}