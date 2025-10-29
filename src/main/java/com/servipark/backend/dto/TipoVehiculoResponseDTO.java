package com.servipark.backend.dto;

import lombok.Data;

/**
 * DTO para enviar información de un TipoVehiculo al cliente.
 */
@Data
public class TipoVehiculoResponseDTO {
    private Long idTipoVehiculo;
    private String nombre;
    private boolean activo;
}