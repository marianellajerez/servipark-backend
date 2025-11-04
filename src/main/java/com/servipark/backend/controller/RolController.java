package com.servipark.backend.controller;

import com.servipark.backend.model.Rol;
import com.servipark.backend.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador para exponer la lista de Roles disponibles
 * (Necesario para los dropdowns del frontend).
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolRepository rolRepository;

    /**
     * [GET] Obtiene todos los roles definidos en el sistema.
     * @return Lista de entidades Rol.
     */
    @GetMapping
    public ResponseEntity<List<Rol>> getAllRoles() {
        return ResponseEntity.ok(rolRepository.findAll());
    }
}