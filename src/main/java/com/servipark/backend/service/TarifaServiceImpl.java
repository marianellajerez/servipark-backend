package com.servipark.backend.service;

import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.repository.TarifaRepository;
import com.servipark.backend.repository.TipoVehiculoRepository;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import com.servipark.backend.exception.ReglaNegocioException;
import com.servipark.backend.exception.ConflictoDeDatosException;
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

    @Override
    @Transactional
    public Tarifa save(Tarifa nuevaTarifa, Long idTipoVehiculo) {
        TipoVehiculo tipoVehiculo = obtenerTipoVehiculoActivo(idTipoVehiculo);
        validarDatosNuevaTarifa(nuevaTarifa);
        cerrarTarifaVigenteSiProcede(tipoVehiculo, nuevaTarifa.getFechaInicio());

        nuevaTarifa.setTipoVehiculo(tipoVehiculo);
        nuevaTarifa.setIdTarifa(null);
        return tarifaRepository.save(nuevaTarifa);
    }

    private TipoVehiculo obtenerTipoVehiculoActivo(Long idTipoVehiculo) {
        return tipoVehiculoRepository.findById(idTipoVehiculo)
                .filter(TipoVehiculo::isActivo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "tarifa.error.tipoVehiculo.noEncontrado.id", idTipoVehiculo
                ));
    }

    private void validarDatosNuevaTarifa(Tarifa nuevaTarifa) {
        if (nuevaTarifa.getFechaInicio() == null || nuevaTarifa.getValorPorMinuto() <= 0) {
            throw new ReglaNegocioException("tarifa.error.validacion.camposRequeridos");
        }
        if (nuevaTarifa.getFechaFin() != null && nuevaTarifa.getFechaFin().isBefore(nuevaTarifa.getFechaInicio())) {
            throw new ReglaNegocioException("tarifa.error.validacion.fechaFinInvalida");
        }
    }

    private void cerrarTarifaVigenteSiProcede(TipoVehiculo tipoVehiculo, LocalDateTime fechaInicioNueva) {
        Optional<Tarifa> tarifaActualOpt = tarifaRepository.findByTipoVehiculoAndFechaFinIsNull(tipoVehiculo);

        if (tarifaActualOpt.isPresent()) {
            Tarifa tarifaActual = tarifaActualOpt.get();

            if (!fechaInicioNueva.isAfter(tarifaActual.getFechaInicio())) {
                throw new ConflictoDeDatosException("tarifa.error.conflicto.fechaInicio");
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