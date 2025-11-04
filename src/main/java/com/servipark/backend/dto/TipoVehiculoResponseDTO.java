package com.servipark.backend.dto;

/**
 * DTO de Respuesta para la entidad TipoVehiculo.
 */
public record TipoVehiculoResponseDTO(
        Long idTipoVehiculo,
        String nombre,
        boolean activo,
        Double valorPorMinutoVigente
) {}
