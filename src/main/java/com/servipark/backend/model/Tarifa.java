package com.servipark.backend.model;

import com.servipark.backend.model.auditoria.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tarifas")
public class Tarifa extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTarifa;

    @Column(nullable = false)
    private Double valorPorMinuto;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @ManyToOne
    @JoinColumn(name = "id_tipo_vehiculo", nullable = false, foreignKey = @ForeignKey(name = "FK_TARIFA_TIPO"))
    private TipoVehiculo tipoVehiculo;
}