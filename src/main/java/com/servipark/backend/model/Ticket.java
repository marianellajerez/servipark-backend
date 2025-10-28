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
@Table(name = "tickets")
public class Ticket extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTicket;

    @Column(nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(nullable = true)
    private LocalDateTime fechaSalida;

    @Column(nullable = true)
    private Double valorTotal;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "FK_TICKET_USUARIO"))
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_vehiculo", nullable = false, foreignKey = @ForeignKey(name = "FK_TICKET_VEHICULO"))
    private Vehiculo vehiculo;

    @ManyToOne
    @JoinColumn(name = "id_tarifa", nullable = false, foreignKey = @ForeignKey(name = "FK_TICKET_TARIFA"))
    private Tarifa tarifa;
}