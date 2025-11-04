package com.servipark.backend.service;

import com.servipark.backend.dto.TarifaCreateDTO;
import com.servipark.backend.model.Tarifa;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TarifaService {
    List<Tarifa> findAll();

    Optional<Tarifa> findById(Long id);

    /**
     * Crea una nueva tarifa, reemplazando la tarifa vigente anterior.
     * La fecha de inicio de vigencia se establece automáticamente como LocalDateTime.now().
     * * @param createDTO DTO con el valor por minuto y el ID del tipo de vehículo.
     * @return La nueva entidad Tarifa creada.
     */
    Tarifa save(TarifaCreateDTO createDTO);

    /**
     * Busca la tarifa que estaba vigente para un tipo de vehículo en una fecha y hora específicas.
     * * @param idTipoVehiculo ID del tipo de vehículo.
     * @param fecha Fecha y hora para la cual se consulta la tarifa.
     * @return Tarifa vigente si existe.
     */
    Optional<Tarifa> findTarifaVigente(Long idTipoVehiculo, LocalDateTime fecha);

    List<Tarifa> findTarifasByTipoVehiculo(Long idTipoVehiculo);

}