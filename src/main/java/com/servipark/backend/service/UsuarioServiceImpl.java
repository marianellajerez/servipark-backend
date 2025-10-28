package com.servipark.backend.service;

import com.servipark.backend.model.Rol;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.repository.RolRepository;
import com.servipark.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

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
            throw new RuntimeException("Error: Ya existe un usuario activo con el correo '" + usuario.getCorreo() + "'.");
        }
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
        usuario.setRol(rol);
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Optional<Usuario> update(Long id, Usuario usuarioDetails, Long idRol) {
        return usuarioRepository.findById(id).map(existingUser -> {
            if (!existingUser.getCorreo().equalsIgnoreCase(usuarioDetails.getCorreo()) &&
                    usuarioRepository.existsByCorreoAndActivoTrue(usuarioDetails.getCorreo())) {
                throw new RuntimeException("Error: Ya existe otro usuario activo con el correo '" + usuarioDetails.getCorreo() + "'.");
            }
            existingUser.setNombre(usuarioDetails.getNombre());
            existingUser.setCorreo(usuarioDetails.getCorreo());
            if (usuarioDetails.getContrasena() != null && !usuarioDetails.getContrasena().isEmpty()) {
                existingUser.setContrasena(usuarioDetails.getContrasena());
            }
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
            existingUser.setRol(rol);
            return usuarioRepository.save(existingUser);
        });
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
}