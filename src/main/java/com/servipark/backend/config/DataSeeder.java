package com.servipark.backend.config;

import com.servipark.backend.model.Rol;
import com.servipark.backend.model.Usuario;
import com.servipark.backend.repository.RolRepository;
import com.servipark.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Locale;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final Locale locale = new Locale("es", "CO");

    @Value("${app.rol.admin}")
    private String ROL_ADMIN;

    @Value("${app.admin.nombre}")
    private String ADMIN_NOMBRE;

    @Value("${app.admin.email}")
    private String ADMIN_EMAIL;

    @Value("${app.admin.password}")
    private String ADMIN_PASSWORD;

    public DataSeeder(RolRepository rolRepository,
                      UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      MessageSource messageSource) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    @Override
    public void run(String... args) throws Exception {
        Rol adminRol = rolRepository.findByNombre(ROL_ADMIN)
                .orElseThrow(() -> {
                    String errorMsg = messageSource.getMessage(
                            "seeder.error.rolNoEncontrado",
                            new Object[]{ROL_ADMIN},
                            locale
                    );
                    return new RuntimeException(errorMsg);
                });

        crearAdminSiNoExiste(adminRol);
    }

    private void crearAdminSiNoExiste(Rol adminRol) {
        if (!usuarioRepository.existsByCorreoAndActivoTrue(ADMIN_EMAIL)) {
            Usuario admin = new Usuario();
            admin.setNombre(ADMIN_NOMBRE);
            admin.setCorreo(ADMIN_EMAIL);
            admin.setContrasena(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRol(adminRol);
            admin.setActivo(true);
            usuarioRepository.save(admin);
        }
    }
}