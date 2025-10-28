package com.servipark.backend.repository;

import com.servipark.backend.model.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {

    Optional<TipoVehiculo> findByNombreAndActivoTrue(String nombre);

    boolean existsByNombreAndActivoTrue(String nombre);

    List<TipoVehiculo> findByActivoTrue();

    Optional<TipoVehiculo> findByIdTipoVehiculo(Long id);

    Optional<TipoVehiculo> findByNombre(String nombre);

}