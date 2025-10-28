package com.servipark.backend.repository;

import com.servipark.backend.model.Tarifa;
import com.servipark.backend.model.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {

    @Query("SELECT t FROM Tarifa t WHERE t.tipoVehiculo = :tipoVehiculo " +
            "AND t.fechaInicio <= :fecha " +
            "AND (t.fechaFin IS NULL OR t.fechaFin >= :fecha)")
    Optional<Tarifa> findTarifaVigente(
            @Param("tipoVehiculo") TipoVehiculo tipoVehiculo,
            @Param("fecha") LocalDateTime fecha);

    List<Tarifa> findByTipoVehiculoAndFechaFinIsNullOrFechaFinGreaterThanEqualAndFechaInicioLessThanEqual(
            TipoVehiculo tipoVehiculo, LocalDateTime fechaInicioNueva, LocalDateTime fechaFinNueva);

}