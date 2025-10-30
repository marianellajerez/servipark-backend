package com.servipark.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar el ingreso de un vehículo (POST /tickets/ingreso).
 * No incluye idUsuario, se extrae del JWT.
 */
public record TicketIngresoCreateDTO(

        @NotBlank(message = "{placa.error.vacia}")
        @Size(max = 10, message = "{placa.error.tamano}")
        String placa,

        @NotNull(message = "{tipovehiculo.error.id.nulo}")
        Long idTipoVehiculo
) {}