package com.servipark.backend.service;

import com.servipark.backend.model.Tarifa;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TarifaService {
    List<Tarifa> findAll();

    Optional<Tarifa> findById(Long id);

    Tarifa save(Tarifa tarifa, Long idTipoVehiculo);

    Optional<Tarifa> findTarifaVigente(Long idTipoVehiculo, LocalDateTime fecha);

}