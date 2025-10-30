package com.servipark.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para registrar la salida de un vehículo (PUT /tickets/salida).
 * Utiliza solo la placa, ya que el idTicket se busca internamente.
 */
public record TicketSalidaCreateDTO(

        @NotBlank(message = "{placa.error.vacia}")
        @Size(max = 10, message = "{placa.error.tamano}")
        String placa
) {}