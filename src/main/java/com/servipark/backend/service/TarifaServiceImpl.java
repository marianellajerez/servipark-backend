package com.servipark.backend.service;

import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.repository.TarifaRepository;
import com.servipark.backend.repository.TipoVehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class TarifaServiceImpl implements TarifaService {

    @Autowired
    private TarifaRepository tarifaRepository;
    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

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
        TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(idTipoVehiculo)
                .filter(TipoVehiculo::isActivo)
                .orElseThrow(() -> new RuntimeException("Error: Tipo de Vehículo activo no encontrado con ID: " + idTipoVehiculo));
        nuevaTarifa.setTipoVehiculo(tipoVehiculo);

        if (nuevaTarifa.getFechaInicio() == null || nuevaTarifa.getValorPorMinuto() <= 0) {
            throw new RuntimeException("Fecha de inicio y valor por minuto positivo son requeridos.");
        }
        if (nuevaTarifa.getFechaFin() != null && nuevaTarifa.getFechaFin().isBefore(nuevaTarifa.getFechaInicio())) {
            throw new RuntimeException("La fecha fin no puede ser anterior a la fecha inicio.");
        }

        Optional<Tarifa> tarifaActualOpt = tarifaRepository.findByTipoVehiculoAndFechaFinIsNull(tipoVehiculo);

        if (tarifaActualOpt.isPresent()) {
            Tarifa tarifaActual = tarifaActualOpt.get();
            if (!nuevaTarifa.getFechaInicio().isAfter(tarifaActual.getFechaInicio())) {
                throw new RuntimeException("La fecha de inicio de la nueva tarifa debe ser posterior a la fecha de inicio de la tarifa actual.");
            }
            tarifaActual.setFechaFin(nuevaTarifa.getFechaInicio().minus(1, ChronoUnit.SECONDS));
            tarifaRepository.save(tarifaActual);
        }

        nuevaTarifa.setIdTarifa(null);
        return tarifaRepository.save(nuevaTarifa);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tarifa> findTarifaVigente(Long idTipoVehiculo, LocalDateTime fecha) {
        TipoVehiculo tipoVehiculo = tipoVehiculoRepository.findById(idTipoVehiculo)
                .orElseThrow(() -> new RuntimeException("Error: Tipo de Vehículo no encontrado."));
        return tarifaRepository.findTarifaVigente(tipoVehiculo, fecha);
    }
}