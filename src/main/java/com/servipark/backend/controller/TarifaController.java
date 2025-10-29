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

import java.time.LocalDateTime;
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
        TarifaResponseDTO response = new TarifaResponseDTO();
        response.setIdTarifa(tarifa.getIdTarifa());
        response.setValorPorMinuto(tarifa.getValorPorMinuto());
        response.setFechaInicio(tarifa.getFechaInicio());
        response.setFechaFin(tarifa.getFechaFin());

        if (tarifa.getTipoVehiculo() != null) {
            response.setIdTipoVehiculo(tarifa.getTipoVehiculo().getIdTipoVehiculo());
            response.setNombreTipoVehiculo(tarifa.getTipoVehiculo().getNombre());
        }
        return response;
    }


    /**
     * [GET] Obtiene todas las tarifas históricas y vigentes.
     * Acceso: Empleado o Admin.
     * @return Lista de TarifaResponseDTO.
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
     * Acceso: Empleado o Admin.
     * @param id ID de la tarifa.
     * @return TarifaResponseDTO si existe, o 404 Not Found.
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
     * Acceso: Empleado (Para cálculos en la caja/entrada).
     * @param idTipoVehiculo ID del tipo de vehículo.
     * @param fecha (Opcional) Fecha y hora para la cual se consulta la tarifa.
     * @return TarifaResponseDTO de la tarifa vigente.
     */
    @GetMapping("/vigente/{idTipoVehiculo}")
    public ResponseEntity<TarifaResponseDTO> getTarifaVigente(
            @PathVariable Long idTipoVehiculo,
            @RequestParam(name = "fecha", required = false) Optional<LocalDateTime> fecha) {

        LocalDateTime fechaConsulta = fecha.orElse(LocalDateTime.now());

        return tarifaService.findTarifaVigente(idTipoVehiculo, fechaConsulta)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * [POST] Crea una nueva tarifa para un TipoVehiculo.
     * La lógica de negocio establece la fechaInicio a LocalDateTime.now() y cierra
     * automáticamente la tarifa anterior.
     *
     * Regla de Negocio: Las tarifas no se modifican ni eliminan; se reemplazan.
     * Acceso: ADMIN.
     *
     * @param createDTO Los datos de la nueva tarifa (solo valor y tipoVehiculoId).
     * @return TarifaResponseDTO del nuevo registro.
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