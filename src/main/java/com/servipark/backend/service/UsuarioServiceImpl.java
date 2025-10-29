package com.servipark.backend.service;

import com.servipark.backend.model.Rol;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.repository.RolRepository;
import com.servipark.backend.repository.UsuarioRepository;
// Importamos las excepciones personalizadas
import com.servipark.backend.exception.ConflictoDeDatosException;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    // Inyección por constructor (SOLID-D)
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario, Long idRol) {
        if (usuarioRepository.existsByCorreoAndActivoTrue(usuario.getCorreo())) {
            throw new ConflictoDeDatosException(
                    "usuario.error.correo.existente", usuario.getCorreo()
            );
        }

        Rol rol = obtenerRolOThrow(idRol);

        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Optional<Usuario> update(Long id, Usuario usuarioDetails, Long idRol) {
        return usuarioRepository.findById(id)
                .map(existingUser -> actualizarCamposYRol(existingUser, usuarioDetails, idRol));
    }

    private Usuario actualizarCamposYRol(Usuario existingUser, Usuario usuarioDetails, Long idRol) {
        boolean isEmailChanging = !existingUser.getCorreo().equalsIgnoreCase(usuarioDetails.getCorreo());

        if (isEmailChanging && usuarioRepository.existsByCorreoAndActivoTrue(usuarioDetails.getCorreo())) {
            throw new ConflictoDeDatosException(
                    "usuario.error.correo.existente", usuarioDetails.getCorreo()
            );
        }

        existingUser.setNombre(usuarioDetails.getNombre());
        existingUser.setCorreo(usuarioDetails.getCorreo());

        if (usuarioDetails.getContrasena() != null && !usuarioDetails.getContrasena().isEmpty()) {
            // Aquí debería ir la lógica de ENCRIPTACIÓN de la contraseña
            existingUser.setContrasena(usuarioDetails.getContrasena());
        }

        Rol rol = obtenerRolOThrow(idRol);
        existingUser.setRol(rol);

        return usuarioRepository.save(existingUser);
    }

    @Override
    @Transactional
    public boolean deactivateById(Long id) {
        return usuarioRepository.findById(id).map(user -> {
            user.setActivo(false);
            usuarioRepository.save(user);
            return true;
        }).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreoAndActivoTrue(correo);
    }

    private Rol obtenerRolOThrow(Long idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "usuario.error.rol.noEncontrado", idRol
                ));
    }
}