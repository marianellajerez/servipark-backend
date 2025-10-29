package com.servipark.backend.service;

import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.TipoVehiculo;
import com.servipark.backend.repository.TarifaRepository;
import com.servipark.backend.repository.TipoVehiculoRepository;
import com.servipark.backend.exception.ConflictoDeDatosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TipoVehiculoServiceImpl implements TipoVehiculoService {

    private final TipoVehiculoRepository tipoVehiculoRepository;
    private final TarifaRepository tarifaRepository;

    @Autowired
    public TipoVehiculoServiceImpl(TipoVehiculoRepository tipoVehiculoRepository,
                                   TarifaRepository tarifaRepository) {
        this.tipoVehiculoRepository = tipoVehiculoRepository;
        this.tarifaRepository = tarifaRepository;
    }

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
            throw new ConflictoDeDatosException(
                    "tipoVehiculo.error.conflicto.nombreExistente", tipoVehiculo.getNombre()
            );
        }
        tipoVehiculo.setActivo(true);
        return tipoVehiculoRepository.save(tipoVehiculo);
    }

    @Override
    @Transactional
    public Optional<TipoVehiculo> update(Long id, TipoVehiculo tipoVehiculoDetails) {
        return tipoVehiculoRepository.findById(id).map(existingTipo -> {

            boolean isNameChanging = !existingTipo.getNombre().equalsIgnoreCase(tipoVehiculoDetails.getNombre());
            boolean newNameExists = tipoVehiculoRepository.existsByNombreAndActivoTrue(tipoVehiculoDetails.getNombre());

            if (isNameChanging && newNameExists) {
                throw new ConflictoDeDatosException(
                        "tipoVehiculo.error.conflicto.nombreExistente", tipoVehiculoDetails.getNombre()
                );
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