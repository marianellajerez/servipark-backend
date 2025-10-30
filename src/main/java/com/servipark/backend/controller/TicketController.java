package com.servipark.backend.controller;

import com.servipark.backend.dto.TicketIngresoCreateDTO;
import com.servipark.backend.dto.TicketSalidaCreateDTO;
import com.servipark.backend.dto.TicketResponseDTO;
import com.servipark.backend.model.Ticket;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.service.TicketService;
import com.servipark.backend.service.UsuarioService;
import com.servipark.backend.exception.ConflictoDeDatosException;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import com.servipark.backend.exception.ReglaNegocioException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final UsuarioService usuarioService;

    private TicketResponseDTO mapToResponse(Ticket ticket) {
        return new TicketResponseDTO(
                ticket.getIdTicket(),
                ticket.getFechaIngreso(),
                ticket.getFechaSalida(),
                ticket.getValorTotal(),
                ticket.getUsuario() != null ? ticket.getUsuario().getId() : null,
                ticket.getUsuario() != null ? ticket.getUsuario().getCorreo() : null,
                ticket.getVehiculo().getIdVehiculo(),
                ticket.getVehiculo().getPlaca(),
                ticket.getVehiculo().getTipoVehiculo().getNombre(),
                ticket.getTarifa().getIdTarifa(),
                ticket.getTarifa().getValorPorMinuto()
        );
    }

    /**
     * Extrae el ID (Usuario.id) del usuario autenticado del token JWT.
     */
    private Long extractUserIdFromPrincipal(Principal principal) {
        String correoUsuario = principal.getName();
        return usuarioService.findByCorreo(correoUsuario)
                .map(Usuario::getId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "auth.error.usuario.noEncontradoConEmail", correoUsuario
                ));
    }

    // --- Endpoints de Transacciones de Parqueo ---

    /**
     * [POST] Registra el ingreso de un vehículo.
     */
    @PostMapping("/ingreso")
    public ResponseEntity<?> registrarIngreso(
            @Valid @RequestBody TicketIngresoCreateDTO ingresoDTO,
            Principal principal) {
        try {
            Long idUsuario = extractUserIdFromPrincipal(principal);

            Ticket nuevoTicket = ticketService.registrarIngreso(
                    ingresoDTO.placa(),
                    ingresoDTO.idTipoVehiculo(),
                    idUsuario
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(nuevoTicket));

        } catch (RecursoNoEncontradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ConflictoDeDatosException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (ReglaNegocioException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + ex.getMessage());
        }
    }

    /**
     * [PUT] Registra la salida de un vehículo usando la placa y calcula el valor total.
     */
    @PutMapping("/salida")
    public ResponseEntity<?> registrarSalida(
            @Valid @RequestBody TicketSalidaCreateDTO salidaDTO,
            Principal principal) {

        try {
            Long idUsuario = extractUserIdFromPrincipal(principal);

            Ticket ticketCerrado = ticketService.registrarSalida(salidaDTO.placa(), idUsuario);
            return ResponseEntity.ok(mapToResponse(ticketCerrado));

        } catch (RecursoNoEncontradoException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (ReglaNegocioException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + ex.getMessage());
        }
    }

    /**
     * [GET] Busca el ticket ACTIVO (sin fechaSalida) por la placa. (Requerimiento: Ticket Activo)
     */
    @GetMapping("/activo/{placa}")
    public ResponseEntity<TicketResponseDTO> getTicketActivoPorPlaca(@PathVariable String placa) {
        return ticketService.findActiveTicketByPlaca(placa)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * [GET] Obtiene todos los tickets (cerrados y activo si existe) para una placa específica.
     * (Requerimiento: Todos los tickets para una placa)
     * * NOTA: Requiere una implementación en TicketService para buscar List<Ticket> por Placa.
     */
    @GetMapping("/historial/{placa}")
    public ResponseEntity<List<TicketResponseDTO>> getHistorialTicketsPorPlaca(@PathVariable String placa) {
        List<Ticket> tickets = ticketService.findAllTicketsByPlaca(placa);

        List<TicketResponseDTO> responseList = tickets.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    /**
     * [GET] Obtiene tickets cerrados en un rango de fechas (Reporte).
     */
    @GetMapping("/cerrados")
    public ResponseEntity<List<TicketResponseDTO>> getTicketsCerrados(
            @RequestParam("fechaInicio") LocalDateTime fechaInicio,
            @RequestParam("fechaFin") LocalDateTime fechaFin) {

        List<TicketResponseDTO> responseList = ticketService.findTicketsCerradosEntreFechas(fechaInicio, fechaFin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}