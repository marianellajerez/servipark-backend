package com.servipark.backend.dto;

import com.servipark.backend.model.Usuario;
import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        boolean activo,
        LocalDateTime fechaCreacion,
        Long idRol,
        String rolNombre
) {
    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.isActivo(),
                usuario.getFechaCreacion(),
                usuario.getRol().getIdRol(),
                usuario.getRol().getNombre()
        );
    }
}