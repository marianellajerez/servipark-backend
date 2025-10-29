package com.servipark.backend.config;

import com.servipark.backend.repository.UsuarioRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class AppConfig {

    private final UsuarioRepository usuarioRepository;
    private final MessageSource messageSource;
    private final Locale locale = new Locale("es", "CO");

    public AppConfig(UsuarioRepository usuarioRepository, MessageSource messageSource) {
        this.usuarioRepository = usuarioRepository;
        this.messageSource = messageSource;
    }

    /**
     * Bean que le dice a Spring Security CÓMO buscar un usuario.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            String notFoundMsg = messageSource.getMessage(
                    "auth.error.usuarioNoEncontrado",
                    new Object[]{username},
                    locale
            );
            return usuarioRepository.findByCorreoAndActivoTrue(username)
                    .orElseThrow(() -> new UsernameNotFoundException(notFoundMsg));
        };
    }

    /**
     * Bean para el codificador de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean que gestiona la autenticación. Usado por el AuthController (Login).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean "Proveedor" que conecta el UserDetailsService y el PasswordEncoder.
     * Usado por el SecurityConfig.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}