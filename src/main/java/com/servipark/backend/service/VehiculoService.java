package com.servipark.backend.service;

import com.servipark.backend.model.Vehiculo;
import java.util.Optional;

public interface VehiculoService {

    Vehiculo findOrCreateVehiculo(String placa, Long idTipoVehiculo);

    Optional<Vehiculo> findByPlaca(String placa);

}