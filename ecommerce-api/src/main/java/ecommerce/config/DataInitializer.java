package ecommerce.config;

import ecommerce.core.domain.Category;
import ecommerce.core.domain.Product;
import ecommerce.core.domain.Role;
import ecommerce.core.domain.User;
import ecommerce.core.repository.RoleRepository;
import ecommerce.core.repository.UserRepository;
import ecommerce.features.category.repository.CategoryRepository;
import ecommerce.features.products.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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

        // 3. Crear Usuarios de prueba
        createTestUser("Mateo", "Cracco", "mateo@test.com", userRole);
        createTestUser("Pepe", "Argento", "pepe@test.com", userRole);
        createTestUser("Jose", "Garcia", "jose@test.com", userRole);

        // 4. Buscar o crear Categorías de forma segura
        Category elect = categoryRepository.findByName("Hardware")
                .orElseGet(() -> categoryRepository.save(new Category(null, "Hardware", true)));


        // 5. Crear Productos iniciales si no existen
        if (productRepository.count() == 0) {

            productRepository.save(Product.builder()
                    .name("Teclado Mecánico RGB")
                    .description("Teclado con switches red, ideal para desarrollo y gaming.")
                    .code("TEC-RGB-01")
                    .price(new BigDecimal("45000.00"))
                    .stock(15)
                    .minStock(5)
                    .imageUrl("https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500")
                    .active(true)
                    .category(elect)
                    .build());

            productRepository.save(Product.builder()
                    .name("Mouse Ergonómico Inalámbrico")
                    .description("Mouse vertical para evitar la fatiga en jornadas largas.")
                    .code("MOU-ERG-02")
                    .price(new BigDecimal("25000.00"))
                    .stock(3)
                    .minStock(5)
                    .imageUrl("https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=500")
                    .active(true)
                    .category(elect)
                    .build());

        }

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