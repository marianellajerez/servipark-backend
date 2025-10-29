package com.servipark.backend.service;

import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.Ticket;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.model.Vehiculo;
import com.servipark.backend.repository.TicketRepository;
import com.servipark.backend.repository.UsuarioRepository;
import com.servipark.backend.exception.ConflictoDeDatosException;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import com.servipark.backend.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoService vehiculoService;
    private final TarifaService tarifaService;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository,
                             UsuarioRepository usuarioRepository,
                             VehiculoService vehiculoService,
                             TarifaService tarifaService) {
        this.ticketRepository = ticketRepository;
        this.usuarioRepository = usuarioRepository;
        this.vehiculoService = vehiculoService;
        this.tarifaService = tarifaService;
    }

    @Override
    @Transactional
    public Ticket registrarIngreso(String placa, Long idTipoVehiculo, Long idUsuario) {
        Vehiculo vehiculo = vehiculoService.findOrCreateVehiculo(placa, idTipoVehiculo);
        validarTicketNoActivo(vehiculo);
        Usuario usuario = obtenerUsuarioActivoOrThrow(idUsuario);
        LocalDateTime ahora = LocalDateTime.now();
        Tarifa tarifa = obtenerTarifaVigenteOrThrow(vehiculo, ahora);
        return crearYGuardarTicketIngreso(vehiculo, usuario, tarifa, ahora);
    }

    @Override
    @Transactional
    public Ticket registrarSalida(Long idTicket, Long idUsuario) {
        Ticket ticket = obtenerTicketActivoOrThrow(idTicket);
        Usuario usuario = obtenerUsuarioActivoOrThrow(idUsuario);
        LocalDateTime ahora = LocalDateTime.now();
        long minutos = calcularMinutosEstacionamiento(ticket.getFechaIngreso(), ahora);
        double valorTotal = calcularValorTotal(ticket.getTarifa(), minutos);
        return actualizarYGuardarTicketSalida(ticket, ahora, valorTotal);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ticket> findActiveTicketByPlaca(String placa) {
        return vehiculoService.findByPlaca(placa)
                .flatMap(ticketRepository::findByVehiculoAndFechaSalidaIsNull);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ticket> findTicketsCerradosEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return ticketRepository.findByFechaSalidaBetween(inicio, fin);
    }

    void validarTicketNoActivo(Vehiculo vehiculo) {
        Optional<Ticket> ticketActivoOpt = ticketRepository.findByVehiculoAndFechaSalidaIsNull(vehiculo);
        if (ticketActivoOpt.isPresent()) {
            throw new ConflictoDeDatosException(
                    "ticket.error.vehiculo.yaActivo", vehiculo.getPlaca()
            );
        }
    }

    Usuario obtenerUsuarioActivoOrThrow(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "ticket.error.usuario.noEncontradoOInactivo", idUsuario
                ));
    }

    Tarifa obtenerTarifaVigenteOrThrow(Vehiculo vehiculo, LocalDateTime fecha) {
        return tarifaService.findTarifaVigente(vehiculo.getTipoVehiculo().getIdTipoVehiculo(), fecha)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "ticket.error.tarifa.noVigente",
                        vehiculo.getTipoVehiculo().getNombre(),
                        fecha.toString()
                ));
    }

    Ticket crearYGuardarTicketIngreso(Vehiculo vehiculo, Usuario usuario, Tarifa tarifa, LocalDateTime fechaIngreso) {
        Ticket nuevoTicket = new Ticket();
        nuevoTicket.setVehiculo(vehiculo);
        nuevoTicket.setUsuario(usuario);
        nuevoTicket.setTarifa(tarifa);
        nuevoTicket.setFechaIngreso(fechaIngreso);
        nuevoTicket.setFechaSalida(null);
        nuevoTicket.setValorTotal(null);
        return ticketRepository.save(nuevoTicket);
    }

    Ticket obtenerTicketActivoOrThrow(Long idTicket) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "ticket.error.ticket.noEncontrado", idTicket
                ));

        if (ticket.getFechaSalida() != null) {
            throw new ReglaNegocioException(
                    "ticket.error.ticket.yaCerrado", idTicket
            );
        }
        return ticket;
    }

    long calcularMinutosEstacionamiento(LocalDateTime fechaIngreso, LocalDateTime fechaSalida) {
        if (fechaSalida.isBefore(fechaIngreso)) {
            fechaSalida = fechaIngreso;
        }
        long minutosExactos = ChronoUnit.MINUTES.between(fechaIngreso, fechaSalida);
        Duration duracionCompleta = Duration.between(fechaIngreso, fechaSalida);
        if (duracionCompleta.getSeconds() % 60 > 0 || duracionCompleta.getNano() > 0) {
            minutosExactos++;
        }
        return Math.max(0, minutosExactos);
    }

    double calcularValorTotal(Tarifa tarifa, long minutos) {
        if (tarifa == null) {
            throw new ReglaNegocioException("ticket.error.tarifa.nula");
        }
        BigDecimal valorPorMinuto = BigDecimal.valueOf(tarifa.getValorPorMinuto());
        BigDecimal minutosBD = BigDecimal.valueOf(minutos);
        BigDecimal valorTotalBD = valorPorMinuto.multiply(minutosBD);
        valorTotalBD = valorTotalBD.setScale(2, RoundingMode.HALF_UP);
        return valorTotalBD.doubleValue();
    }

    Ticket actualizarYGuardarTicketSalida(Ticket ticket, LocalDateTime fechaSalida, double valorTotal) {
        ticket.setFechaSalida(fechaSalida);
        ticket.setValorTotal(valorTotal);
        return ticketRepository.save(ticket);
    }
}