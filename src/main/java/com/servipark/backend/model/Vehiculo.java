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

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "vehiculos")
public class Vehiculo extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehiculo;

    @Column(length = 10, nullable = false, unique = true)
    private String placa;

    @ManyToOne
    @JoinColumn(name = "id_tipo_vehiculo", nullable = false, foreignKey = @ForeignKey(name = "FK_VEHICULO_TIPO"))
    private TipoVehiculo tipoVehiculo;
}