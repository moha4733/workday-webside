package dk.tommer.workday.config;

import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            logger.info("DataInitializer started - checking for admin user...");
            String adminEmail = "admin@workday.dk";
            String adminPassword = "admin123";
            String svendEmail = "svend@workday.dk";
            String svendPassword = "svend123";

            // Check if admin user already exists
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                logger.info("Admin user not found. Creating default admin user...");
                
                User admin = new User();
                admin.setName("System Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                
                User savedUser = userRepository.save(admin);
                logger.info("=== Admin user created successfully ===");
                logger.info("ID: {}", savedUser.getId());
                logger.info("Email: {}", adminEmail);
                logger.info("Password: {}", adminPassword);
                logger.info("Role: {}", savedUser.getRole());
            } else {
                // Update existing admin user to ensure it has the correct role and password
                User existingAdmin = userRepository.findByEmail(adminEmail).get();
                logger.info("Admin user found with ID: {} and role: {}", existingAdmin.getId(), existingAdmin.getRole());
                
                boolean needsUpdate = false;
                if (existingAdmin.getRole() != Role.ADMIN) {
                    logger.info("Updating existing user to admin role...");
                    existingAdmin.setRole(Role.ADMIN);
                    needsUpdate = true;
                }
                
                // Always update password to ensure it's correctly encoded
                logger.info("Updating admin password to ensure correct encoding...");
                existingAdmin.setPassword(passwordEncoder.encode(adminPassword));
                needsUpdate = true;
                
                if (needsUpdate) {
                    userRepository.save(existingAdmin);
                    logger.info("Admin user updated with correct role and password");
                } else {
                    logger.info("Admin user already exists with correct role");
                }
            }
            logger.info("DataInitializer completed successfully");
            if (userRepository.findByEmail(svendEmail).isEmpty()) {
                User svend = new User();
                svend.setName("Svend Bruger");
                svend.setEmail(svendEmail);
                svend.setPassword(passwordEncoder.encode(svendPassword));
                svend.setRole(Role.SVEND);
                userRepository.save(svend);
                logger.info("Svend user created: {} / {}", svendEmail, svendPassword);
            }

            // Appen bruger kun ADMIN + SVEND. Sørg for at alle ikke-admin brugere har SVEND-rolle,
            // så ældre data (USER/null) ikke giver uventet flow efter login.
            userRepository.findAll().stream()
                    .filter(u -> u.getEmail() != null && !adminEmail.equalsIgnoreCase(u.getEmail()))
                    .filter(u -> u.getRole() == null || u.getRole() == Role.USER)
                    .forEach(u -> {
                        logger.info("Upgrading user role to SVEND for: {} (was: {})", u.getEmail(), u.getRole());
                        u.setRole(Role.SVEND);
                        userRepository.save(u);
                    });
        } catch (Exception e) {
            logger.error("Error in DataInitializer: {}", e.getMessage(), e);
            throw e;
        }
    }
}
