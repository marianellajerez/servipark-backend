package com.servipark.backend.controller;

import com.servipark.backend.dto.AuthResponseDTO;
import com.servipark.backend.dto.LoginRequestDTO;
import com.servipark.backend.security.JwtService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Hidden // <-- Oculta este controlador de Swagger
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.correo(),
                        request.contrasena()
                )
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}