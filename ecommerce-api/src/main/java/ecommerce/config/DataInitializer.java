package ecommerce.config;

import ecommerce.core.domain.Role;
import ecommerce.core.domain.User;
import ecommerce.core.repository.RoleRepository;
import ecommerce.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 1. Crear Roles si no existen
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

        // 2. Crear Administrador
        if (!userRepository.existsByEmail("admin@ecommerce.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setLastName("Sistema");
            admin.setEmail("admin@ecommerce.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole, userRole));
            userRepository.save(admin);
        }

        // 3. Crear 3 Usuarios de prueba
        createTestUser("Mateo", "Cracco", "mateo@test.com", userRole);
        createTestUser("Pepe", "Argento", "pepe@test.com", userRole);
        createTestUser("Jose", "Garcia", "jose@test.com", userRole);

        System.out.println("--- DATOS DE PRUEBA CARGADOS CORRECTAMENTE EN MYSQL ---");
    }

    private void createTestUser(String name, String lastName, String email, Role role) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setName(name);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEnabled(true);
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }
    }


}