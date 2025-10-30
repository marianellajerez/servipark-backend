package com.servipark.backend.dto;

/**
 * DTO de Respuesta para la entidad TipoVehiculo.
 * Implementado como un record para asegurar la inmutabilidad.
 */
public record TipoVehiculoResponseDTO(
        Long idTipoVehiculo,
        String nombre,
        boolean activo
) {}
