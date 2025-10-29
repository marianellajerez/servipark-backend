package com.servipark.backend.service;

import com.servipark.backend.model.Rol;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.repository.RolRepository;
import com.servipark.backend.repository.UsuarioRepository;
import com.servipark.backend.exception.ConflictoDeDatosException;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
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
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

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
            existingUser.setContrasena(
                    passwordEncoder.encode(usuarioDetails.getContrasena())
            );
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