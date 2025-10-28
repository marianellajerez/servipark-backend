package com.servipark.backend.service;

import com.servipark.backend.model.Ticket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketService {

    Ticket registrarIngreso(String placa, Long idTipoVehiculo, Long idUsuario);

    Ticket registrarSalida(Long idTicket, Long idUsuario);

    Optional<Ticket> findActiveTicketByPlaca(String placa);

    List<Ticket> findTicketsCerradosEntreFechas(LocalDateTime inicio, LocalDateTime fin);

}