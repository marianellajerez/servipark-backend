package com.servipark.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO utilizado para devolver la información detallada de una Tarifa,
 * incluyendo sus fechas de vigencia y el tipo de vehículo asociado.
 */
@Data
public class TarifaResponseDTO {
    private Long idTarifa;
    private Double valorPorMinuto;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Long idTipoVehiculo;
    private String nombreTipoVehiculo;
}