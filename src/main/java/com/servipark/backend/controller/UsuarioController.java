package com.servipark.backend.controller;

import com.servipark.backend.dto.UsuarioCreateDTO;
import com.servipark.backend.dto.UsuarioResponseDTO;
import com.servipark.backend.dto.UsuarioUpdateDTO;
import com.servipark.backend.exception.RecursoNoEncontradoException;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAllActiveUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.findAll()
                .stream()
                .map(UsuarioResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("usuario.error.noEncontrado.id", id));

        return ResponseEntity.ok(UsuarioResponseDTO.from(usuario));
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioByCorreo(@PathVariable String correo) {
        Usuario usuario = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("usuario.error.noEncontrado.correo", correo));

        return ResponseEntity.ok(UsuarioResponseDTO.from(usuario));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@Valid @RequestBody UsuarioCreateDTO dto) {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(dto.nombre());
        nuevoUsuario.setCorreo(dto.correo());
        nuevoUsuario.setContrasena(dto.contrasena());

        Usuario usuarioGuardado = usuarioService.save(nuevoUsuario, dto.idRol());

        return new ResponseEntity<>(
                UsuarioResponseDTO.from(usuarioGuardado),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@PathVariable Long id,
                                                            @Valid @RequestBody UsuarioUpdateDTO dto) {
        Usuario usuarioDetails = new Usuario();
        usuarioDetails.setNombre(dto.nombre());
        usuarioDetails.setCorreo(dto.correo());
        usuarioDetails.setContrasena(dto.contrasena());

        Usuario usuarioActualizado = usuarioService.update(id, usuarioDetails, dto.idRol())
                .orElseThrow(() -> new RecursoNoEncontradoException("usuario.error.noEncontrado.id", id));

        return ResponseEntity.ok(UsuarioResponseDTO.from(usuarioActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUsuario(@PathVariable Long id) {
        boolean deactivated = usuarioService.deactivateById(id);
        if (!deactivated) {
            throw new RecursoNoEncontradoException("usuario.error.noEncontrado.id", id);
        }
        return ResponseEntity.noContent().build();
    }
}