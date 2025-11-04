package com.servipark.backend.controller;

import com.servipark.backend.dto.TipoVehiculoConTarifaCreateDTO;
import com.servipark.backend.dto.TipoVehiculoCreateDTO;
import com.servipark.backend.dto.TipoVehiculoResponseDTO;
import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.repository.TarifaRepository;
import com.servipark.backend.service.TipoVehiculoService;
import com.servipark.backend.exception.ConflictoDeDatosException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión CRUD de Tipos de Vehículo.
 * (Actualizado para incluir tarifas)
 */
@RestController
@RequestMapping("/api/v1/tipos-vehiculo")
@RequiredArgsConstructor
public class TipoVehiculoController {

    private final TipoVehiculoService tipoVehiculoService;
    private final TarifaRepository tarifaRepository; // <-- INYECTADO

    /**
     * Mapeador auxiliar (MODIFICADO para incluir la tarifa vigente)
     */
    private TipoVehiculoResponseDTO mapToResponse(TipoVehiculo tipoVehiculo) {

        Optional<Tarifa> tarifaVigenteOpt = tarifaRepository.findTarifaVigente(
                tipoVehiculo,
                LocalDateTime.now(ZoneOffset.UTC)
        );

        Double valorVigente = tarifaVigenteOpt
                .map(Tarifa::getValorPorMinuto)
                .orElse(null);

        return new TipoVehiculoResponseDTO(
                tipoVehiculo.getIdTipoVehiculo(),
                tipoVehiculo.getNombre(),
                tipoVehiculo.isActivo(),
                valorVigente
        );
    }

    /**
     * Mapeador auxiliar (usado solo para el PUT de actualizar nombre)
     */
    private TipoVehiculo mapCreateToEntity(TipoVehiculoCreateDTO createDTO) {
        TipoVehiculo tipoVehiculo = new TipoVehiculo();
        tipoVehiculo.setNombre(createDTO.nombre());
        return tipoVehiculo;
    }

    /**
     * [GET] Obtiene TODOS los tipos de vehículo (ahora con tarifa vigente).
     */
    @GetMapping
    public ResponseEntity<List<TipoVehiculoResponseDTO>> getAllTiposVehiculo() {
        List<TipoVehiculoResponseDTO> responseList = tipoVehiculoService.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    /**
     * [GET] Obtiene un tipo de vehículo por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoVehiculoResponseDTO> getTipoVehiculoById(@PathVariable Long id) {
        return tipoVehiculoService.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * [POST] Crea un nuevo tipo de vehículo y su tarifa inicial.
     */
    @PostMapping
    public ResponseEntity<?> createTipoVehiculo(@Valid @RequestBody TipoVehiculoConTarifaCreateDTO createDTO) {
        try {
            TipoVehiculo savedTipo = tipoVehiculoService.saveWithInitialTarifa(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedTipo));
        } catch (ConflictoDeDatosException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + ex.getMessage());
        }
    }

    /**
     * [PUT] Edita/Actualiza el nombre de un tipo de vehículo.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTipoVehiculo(@PathVariable Long id, @Valid @RequestBody TipoVehiculoCreateDTO updateDTO) {
        try {
            TipoVehiculo updateDetails = mapCreateToEntity(updateDTO);

            Optional<TipoVehiculoResponseDTO> response = tipoVehiculoService.update(id, updateDetails)
                    .map(this::mapToResponse);

            return response.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (ConflictoDeDatosException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + ex.getMessage());
        }
    }

    /**
     * [DELETE] Desactiva lógicamente un tipo de vehículo.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateTipoVehiculo(@PathVariable Long id) {
        try {
            boolean deactivated = tipoVehiculoService.deactivateById(id);
            if (deactivated) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * [PUT] Reactiva lógicamente un tipo de vehículo.
     */
    @PutMapping("/{id}/activar")
    public ResponseEntity<Void> activateTipoVehiculo(@PathVariable Long id) {
        try {
            boolean activated = tipoVehiculoService.activateById(id);
            if (activated) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}