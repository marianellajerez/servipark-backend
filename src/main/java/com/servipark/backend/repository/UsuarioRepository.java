package com.servipark.backend.repository;

import com.servipark.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoAndActivoTrue(String correo);

    boolean existsByCorreoAndActivoTrue(String correo);

    List<Usuario> findByActivoTrue();

}