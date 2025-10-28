package com.servipark.backend.service;

import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.repository.TarifaRepository;
import com.servipark.backend.repository.TipoVehiculoRepository;
import com.servipark.backend.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TipoVehiculoServiceImpl implements TipoVehiculoService {

    @Autowired
    private TipoVehiculoRepository tipoVehiculoRepository;

    @Autowired
    private VehiculoRepository vehiculoRepository;

    @Autowired
    private TarifaRepository tarifaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoVehiculo> findAll() {
        return tipoVehiculoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoVehiculo> findById(Long id) {
        return tipoVehiculoRepository.findById(id);
    }

    @Override
    @Transactional
    public TipoVehiculo save(TipoVehiculo tipoVehiculo) {
        if (tipoVehiculoRepository.existsByNombreAndActivoTrue(tipoVehiculo.getNombre())) {
            throw new RuntimeException("Error: Ya existe un tipo de vehículo activo con el nombre '" + tipoVehiculo.getNombre() + "'.");
        }
        tipoVehiculo.setActivo(true);
        return tipoVehiculoRepository.save(tipoVehiculo);
    }

    @Override
    @Transactional
    public Optional<TipoVehiculo> update(Long id, TipoVehiculo tipoVehiculoDetails) {
        return tipoVehiculoRepository.findById(id).map(existingTipo -> {
            if (!existingTipo.getNombre().equalsIgnoreCase(tipoVehiculoDetails.getNombre()) &&
                    tipoVehiculoRepository.existsByNombreAndActivoTrue(tipoVehiculoDetails.getNombre())) {
                throw new RuntimeException("Error: Ya existe otro tipo de vehículo activo con el nombre '" + tipoVehiculoDetails.getNombre() + "'.");
            }
            existingTipo.setNombre(tipoVehiculoDetails.getNombre());
            return tipoVehiculoRepository.save(existingTipo);
        });
    }

    @Override
    @Transactional
    public boolean deactivateById(Long id) {
        return tipoVehiculoRepository.findById(id).map(tipo -> {
            Optional<Tarifa> tarifaActivaOpt = tarifaRepository.findByTipoVehiculoAndFechaFinIsNull(tipo);
            if (tarifaActivaOpt.isPresent()) {
                Tarifa tarifaActiva = tarifaActivaOpt.get();
                tarifaActiva.setFechaFin(LocalDateTime.now());
                tarifaRepository.save(tarifaActiva);
            }
            tipo.setActivo(false);
            tipoVehiculoRepository.save(tipo);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoVehiculo> findByNombre(String nombre) {
        return tipoVehiculoRepository.findByNombreAndActivoTrue(nombre);
    }
}