package com.servipark.backend.service;

import com.servipark.backend.dto.TipoVehiculoConTarifaCreateDTO;
import com.servipark.backend.model.TipoVehiculo;
import java.util.List;
import java.util.Optional;

public interface TipoVehiculoService {

    List<TipoVehiculo> findAll();

    Optional<TipoVehiculo> findById(Long id);

    TipoVehiculo save(TipoVehiculo tipoVehiculo);

    TipoVehiculo saveWithInitialTarifa(TipoVehiculoConTarifaCreateDTO createDTO);

    Optional<TipoVehiculo> update(Long id, TipoVehiculo tipoVehiculoDetails);

    boolean deactivateById(Long id);

    boolean activateById(Long id);

    Optional<TipoVehiculo> findByNombre(String nombre);
}
