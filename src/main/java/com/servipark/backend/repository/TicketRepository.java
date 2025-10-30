package com.servipark.backend.repository;

import com.servipark.backend.model.Ticket;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByVehiculoAndFechaSalidaIsNull(Vehiculo vehiculo);

    List<Ticket> findByFechaSalidaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Ticket> findByUsuarioAndFechaIngresoBetween(Usuario usuario, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Ticket> findByVehiculo_TipoVehiculo_IdTipoVehiculoAndFechaSalidaBetween(Long idTipoVehiculo, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Ticket> findByVehiculo(Vehiculo vehiculo);
}