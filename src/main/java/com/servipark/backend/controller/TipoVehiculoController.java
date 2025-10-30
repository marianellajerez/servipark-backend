package com.servipark.backend.controller;

import com.servipark.backend.dto.TipoVehiculoCreateDTO;
import com.servipark.backend.dto.TipoVehiculoResponseDTO;
import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.service.TipoVehiculoService;
import com.servipark.backend.exception.ConflictoDeDatosException;
import com.servipark.backend.exception.RecursoNoEncontradoException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tipos-vehiculo")
@RequiredArgsConstructor
public class TipoVehiculoController {

    private final TipoVehiculoService tipoVehiculoService;

    /**
     * Convierte una entidad TipoVehiculo a un TipoVehiculoResponseDTO.
     */
    private TipoVehiculoResponseDTO mapToResponse(TipoVehiculo tipoVehiculo) {
        return new TipoVehiculoResponseDTO(
                tipoVehiculo.getIdTipoVehiculo(),
                tipoVehiculo.getNombre(),
                tipoVehiculo.isActivo()
        );
    }

    /**
     * Convierte un TipoVehiculoCreateDTO a una entidad TipoVehiculo.
     */
    private TipoVehiculo mapCreateToEntity(TipoVehiculoCreateDTO createDTO) {
        TipoVehiculo tipoVehiculo = new TipoVehiculo();
        tipoVehiculo.setNombre(createDTO.nombre());
        return tipoVehiculo;
    }

    /**
     * [GET] Obtiene todos los tipos de vehículo ACTIVOS.
     * Acceso: General (necesario para el front-end, por ejemplo).
     * @return Lista de TipoVehiculoResponseDTO (solo activos).
     */
    @GetMapping
    public ResponseEntity<List<TipoVehiculoResponseDTO>> getAllTiposVehiculoActivos() {
        List<TipoVehiculoResponseDTO> responseList = tipoVehiculoService.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    /**
     * [GET] Obtiene un tipo de vehículo por su ID.
     * Acceso: Empleado o Admin.
     * @param id ID del tipo de vehículo.
     * @return TipoVehiculoResponseDTO si existe, o 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoVehiculoResponseDTO> getTipoVehiculoById(@PathVariable Long id) {
        return tipoVehiculoService.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * [POST] Crea un nuevo tipo de vehículo.
     * Regla de Negocio: Solo Admin. Lanza ConflictoDeDatosException si el nombre ya existe (activo).
     * @param createDTO Datos del nuevo tipo de vehículo.
     * @return El TipoVehiculoResponseDTO creado.
     */
    @PostMapping
    public ResponseEntity<?> createTipoVehiculo(@Valid @RequestBody TipoVehiculoCreateDTO createDTO) {
        try {
            TipoVehiculo newTipo = mapCreateToEntity(createDTO);
            TipoVehiculo savedTipo = tipoVehiculoService.save(newTipo);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedTipo));
        } catch (ConflictoDeDatosException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + ex.getMessage());
        }
    }

    /**
     * [PUT] Edita/Actualiza un tipo de vehículo existente.
     * Regla de Negocio: Solo Admin.
     * @param id ID del tipo a editar.
     * @param updateDTO Datos actualizados (mismo DTO ya que solo se edita el nombre).
     * @return El TipoVehiculoResponseDTO actualizado.
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
     * [DELETE] Desactiva lógicamente (soft delete) un tipo de vehículo.
     * Regla de Negocio: No se elimina, solo se desactiva. Al desactivar, la tarifa vigente se cierra.
     * Acceso: ADMIN.
     * @param id ID del tipo de vehículo a desactivar.
     * @return 204 No Content si la desactivación es exitosa.
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
}