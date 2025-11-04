package com.servipark.backend.controller;

import com.servipark.backend.dto.TarifaCreateDTO;
import com.servipark.backend.dto.TarifaResponseDTO;
import com.servipark.backend.model.Tarifa;
import com.servipark.backend.service.TarifaService;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import com.servipark.backend.exception.ConflictoDeDatosException;
import com.servipark.backend.exception.ReglaNegocioException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat; // <-- 1. AÑADIR IMPORTACIÓN

import java.time.LocalDateTime;
import java.time.ZoneOffset; // <-- IMPORTACIÓN NECESARIA
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaService tarifaService;

    /**
     * Convierte una entidad Tarifa a un TarifaResponseDTO.
     */
    private TarifaResponseDTO mapToResponse(Tarifa tarifa) {
        return new TarifaResponseDTO(
                tarifa.getIdTarifa(),
                tarifa.getValorPorMinuto(),
                tarifa.getFechaInicio(),
                tarifa.getFechaFin(),
                tarifa.getTipoVehiculo() != null ? tarifa.getTipoVehiculo().getIdTipoVehiculo() : null,
                tarifa.getTipoVehiculo() != null ? tarifa.getTipoVehiculo().getNombre() : null
        );
    }

    /**
     * [GET] Obtiene todas las tarifas históricas y vigentes.
     */
    @GetMapping
    public ResponseEntity<List<TarifaResponseDTO>> getAllTarifas() {
        List<TarifaResponseDTO> responseList = tarifaService.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    /**
     * [GET] Obtiene una tarifa específica por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TarifaResponseDTO> getTarifaById(@PathVariable Long id) {
        return tarifaService.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * [GET] Obtiene la tarifa VIGENTE para un tipo de vehículo en una fecha específica (o actual).
     */
    @GetMapping("/vigente/{idTipoVehiculo}")
    public ResponseEntity<TarifaResponseDTO> getTarifaVigente(
            @PathVariable Long idTipoVehiculo,
            // --- 2. AÑADIR ANOTACIÓN DE FORMATO ---
            @RequestParam(name = "fecha", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {

        LocalDateTime fechaConsulta = (fecha != null) ? fecha : LocalDateTime.now(ZoneOffset.UTC);

        return tarifaService.findTarifaVigente(idTipoVehiculo, fechaConsulta)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * [GET] Obtiene el historial de tarifas para un solo Tipo de Vehículo.
     */
    @GetMapping("/historial/{idTipoVehiculo}")
    public ResponseEntity<List<TarifaResponseDTO>> getHistorialTarifasPorTipo(
            @PathVariable Long idTipoVehiculo) {

        List<TarifaResponseDTO> responseList = tarifaService.findTarifasByTipoVehiculo(idTipoVehiculo)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    /**
     * [POST] Crea una nueva tarifa para un TipoVehiculo.
     */
    @PostMapping
    public ResponseEntity<?> createTarifa(@Valid @RequestBody TarifaCreateDTO createDTO) {
        try {
            Tarifa savedTarifa = tarifaService.save(createDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedTarifa));

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
}
