package com.servipark.backend.service;

import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.repository.TarifaRepository;
import com.servipark.backend.repository.TipoVehiculoRepository;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import com.servipark.backend.exception.ConflictoDeDatosException;
import com.servipark.backend.dto.TarifaCreateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TarifaServiceImpl implements TarifaService {

    private final TarifaRepository tarifaRepository;
    private final TipoVehiculoRepository tipoVehiculoRepository;

    @Autowired
    public TarifaServiceImpl(TarifaRepository tarifaRepository,
                             TipoVehiculoRepository tipoVehiculoRepository) {
        this.tarifaRepository = tarifaRepository;
        this.tipoVehiculoRepository = tipoVehiculoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tarifa> findAll() {
        return tarifaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tarifa> findById(Long id) {
        return tarifaRepository.findById(id);
    }

    /**
     * Implementa la lógica de reemplazo: establece la fechaInicio a LocalDateTime.now(),
     * cierra la tarifa anterior (si existe) y crea la nueva tarifa como vigente.
     */
    @Override
    @Transactional
    public Tarifa save(TarifaCreateDTO createDTO) {
        TipoVehiculo tipoVehiculo = obtenerTipoVehiculoActivo(createDTO.idTipoVehiculo());
        LocalDateTime fechaInicioNueva = LocalDateTime.now();
        cerrarTarifaVigenteSiProcede(tipoVehiculo, fechaInicioNueva);
        Tarifa nuevaTarifa = new Tarifa();
        nuevaTarifa.setValorPorMinuto(createDTO.valorPorMinuto());
        nuevaTarifa.setFechaInicio(fechaInicioNueva); // Fecha generada por la aplicación
        nuevaTarifa.setTipoVehiculo(tipoVehiculo);
        nuevaTarifa.setIdTarifa(null);
        nuevaTarifa.setFechaFin(null); // La nueva tarifa siempre es VIGENTE al inicio

        return tarifaRepository.save(nuevaTarifa);
    }

    private TipoVehiculo obtenerTipoVehiculoActivo(Long idTipoVehiculo) {
        return tipoVehiculoRepository.findById(idTipoVehiculo)
                .filter(TipoVehiculo::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "tarifa.error.tipoVehiculo.noEncontrado.id", idTipoVehiculo
                ));
    }

    /**
     * Regla de Negocio CLAVE: Cierra la tarifa actualmente VIGENTE (fechaFin = NULL)
     * para el TipoVehiculo dado, si existe.
     * * Se valida que la nueva fecha de inicio sea posterior a la anterior.
     */
    private void cerrarTarifaVigenteSiProcede(TipoVehiculo tipoVehiculo, LocalDateTime fechaInicioNueva) {
        Optional<Tarifa> tarifaActualOpt = tarifaRepository.findByTipoVehiculoAndFechaFinIsNull(tipoVehiculo);

        if (tarifaActualOpt.isPresent()) {
            Tarifa tarifaActual = tarifaActualOpt.get();

            // Validación de Regla de Negocio: La nueva tarifa debe ser cronológicamente posterior.
            if (!fechaInicioNueva.isAfter(tarifaActual.getFechaInicio())) {
                throw new ConflictoDeDatosException(
                        "tarifa.error.conflicto.fechaInicio",
                        "La fecha de inicio de la nueva tarifa debe ser estrictamente posterior a la fecha de inicio de la tarifa vigente actual."
                );
            }

            tarifaActual.setFechaFin(fechaInicioNueva.minus(1, ChronoUnit.SECONDS));
            tarifaRepository.save(tarifaActual);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tarifa> findTarifaVigente(Long idTipoVehiculo, LocalDateTime fecha) {
        TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(idTipoVehiculo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "tarifa.error.tipoVehiculo.noEncontrado"
                ));
        return tarifaRepository.findTarifaVigente(tipoVehiculo, fecha);
    }
}