package com.servipark.backend.dto;

import com.servipark.backend.model.Usuario;

/**
 * DTO de Respuesta para la entidad Usuario.
 * Implementado como un record para asegurar la inmutabilidad.
 */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String correo,
        String rol,
        boolean activo
) {
    /**
     * Método helper estático para convertir fácilmente una entidad Usuario
     * a un DTO de respuesta de Usuario.
     */
    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre(),
                usuario.isActivo()
        );
    }
}
