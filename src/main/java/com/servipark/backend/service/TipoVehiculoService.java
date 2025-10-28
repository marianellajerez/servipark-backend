package com.servipark.backend.service;

import com.servipark.backend.model.TipoVehiculo;
import java.util.List;
import java.util.Optional;

public interface TipoVehiculoService {

    List<TipoVehiculo> findAll();

    Optional<TipoVehiculo> findById(Long id);

    TipoVehiculo save(TipoVehiculo tipoVehiculo);

    Optional<TipoVehiculo> update(Long id, TipoVehiculo tipoVehiculo);

    boolean deactivateById(Long id);

    Optional<TipoVehiculo> findByNombre(String nombre);

}