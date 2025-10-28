package com.servipark.backend.repository;

import com.servipark.backend.model.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {

    Optional<TipoVehiculo> findByNombre(String nombre);

}