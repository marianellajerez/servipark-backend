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

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat; // <-- 1. AÑADIR IMPORTACIÓN

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

    // (El método mapToResponse no cambia)
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

    // (El método extractUserIdFromPrincipal no cambia)
    private Long extractUserIdFromPrincipal(Principal principal) {
        String correoUsuario = principal.getName();
        return usuarioService.findByCorreo(correoUsuario)
                .map(Usuario::getId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "auth.error.usuario.noEncontradoConEmail", correoUsuario
                ));
    }

    @PostMapping("/ingreso")
    public ResponseEntity<?> registrarIngreso(
            @Valid @RequestBody TicketIngresoCreateDTO ingresoDTO,
            @Parameter(hidden = true) Principal principal) {
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

    @PutMapping("/salida")
    public ResponseEntity<?> registrarSalida(
            @Valid @RequestBody TicketSalidaCreateDTO salidaDTO,
            @Parameter(hidden = true) Principal principal) {

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

    @GetMapping("/activo/{placa}")
    public ResponseEntity<TicketResponseDTO> getTicketActivoPorPlaca(@PathVariable String placa) {
        return ticketService.findActiveTicketByPlaca(placa)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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
            // --- 2. AÑADIR ANOTACIÓN DE FORMATO ---
            @RequestParam("fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<TicketResponseDTO> responseList = ticketService.findTicketsCerradosEntreFechas(fechaInicio, fechaFin).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}
