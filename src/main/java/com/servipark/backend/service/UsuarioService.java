package com.servipark.backend.service;

import com.servipark.backend.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> findAll();

    Optional<Usuario> findById(Long id);

    Usuario save(Usuario usuario, Long idRol);

    Optional<Usuario> update(Long id, Usuario usuarioDetails, Long idRol);

    boolean deactivateById(Long id);

    Optional<Usuario> findByCorreo(String correo);

}