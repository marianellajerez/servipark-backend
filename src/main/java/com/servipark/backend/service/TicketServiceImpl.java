package com.servipark.backend.service;

import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.Ticket;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.model.Vehiculo;
import com.servipark.backend.repository.TicketRepository;
import com.servipark.backend.repository.UsuarioRepository;
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
        Usuario usuario = obtenerUsuarioActivoOrThrow(idUsuario); // Valida el usuario que cierra
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

    private void validarTicketNoActivo(Vehiculo vehiculo) {
        Optional<Ticket> ticketActivoOpt = ticketRepository.findByVehiculoAndFechaSalidaIsNull(vehiculo);
        if (ticketActivoOpt.isPresent()) {
            throw new RuntimeException("Error: El vehículo con placa " + vehiculo.getPlaca() + " ya tiene un ticket activo.");
        }
    }

    private Usuario obtenerUsuarioActivoOrThrow(Long idUsuario) {
        return usuarioRepository.findByIdUsuarioAndActivoTrue(idUsuario)
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado o inactivo con ID: " + idUsuario));
    }

    private Tarifa obtenerTarifaVigenteOrThrow(Vehiculo vehiculo, LocalDateTime fecha) {
        return tarifaService.findTarifaVigente(vehiculo.getTipoVehiculo().getIdTipoVehiculo(), fecha)
                .orElseThrow(() -> new RuntimeException("Error: No se encontró tarifa vigente para el tipo de vehículo "
                        + vehiculo.getTipoVehiculo().getNombre() + " en la fecha " + fecha));
    }

    private Ticket crearYGuardarTicketIngreso(Vehiculo vehiculo, Usuario usuario, Tarifa tarifa, LocalDateTime fechaIngreso) {
        Ticket nuevoTicket = new Ticket();
        nuevoTicket.setVehiculo(vehiculo);
        nuevoTicket.setUsuario(usuario);
        nuevoTicket.setTarifa(tarifa);
        nuevoTicket.setFechaIngreso(fechaIngreso);
        nuevoTicket.setFechaSalida(null);
        nuevoTicket.setValorTotal(null);
        return ticketRepository.save(nuevoTicket);
    }

    private Ticket obtenerTicketActivoOrThrow(Long idTicket) {
        Ticket ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Error: Ticket no encontrado con ID: " + idTicket));
        if (ticket.getFechaSalida() != null) {
            throw new RuntimeException("Error: El ticket con ID " + idTicket + " ya ha sido cerrado.");
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

    private double calcularValorTotal(Tarifa tarifa, long minutos) {
        if (tarifa == null) {
            throw new RuntimeException("Error: No se puede calcular el valor total sin una tarifa.");
        }
        BigDecimal valorPorMinuto = BigDecimal.valueOf(tarifa.getValorPorMinuto());
        BigDecimal minutosBD = BigDecimal.valueOf(minutos);
        BigDecimal valorTotalBD = valorPorMinuto.multiply(minutosBD);
        valorTotalBD = valorTotalBD.setScale(2, RoundingMode.HALF_UP);
        return valorTotalBD.doubleValue();
    }

    private Ticket actualizarYGuardarTicketSalida(Ticket ticket, LocalDateTime fechaSalida, double valorTotal) {
        ticket.setFechaSalida(fechaSalida);
        ticket.setValorTotal(valorTotal);
        return ticketRepository.save(ticket);
    }
}