package com.servipark.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO de Respuesta para la entidad Ticket.
 * Implementado como un record para asegurar la inmutabilidad.
 */
public record TicketResponseDTO(
        Long idTicket,
        LocalDateTime fechaIngreso,
        LocalDateTime fechaSalida,
        Double valorTotal,
        Long idUsuario,
        String emailUsuario,
        Long idVehiculo,
        String placaVehiculo,
        String tipoVehiculo,
        Long idTarifa,
        Double valorPorMinutoTarifa
) {}
