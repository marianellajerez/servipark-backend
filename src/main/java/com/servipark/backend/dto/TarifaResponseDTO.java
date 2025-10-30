package com.servipark.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO de Respuesta para la entidad Tarifa.
 * Implementado como un record para asegurar la inmutabilidad.
 */
public record TarifaResponseDTO(
        Long idTarifa,
        Double valorPorMinuto,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Long idTipoVehiculo,
        String nombreTipoVehiculo
) {}
