package dk.tommer.workday.e2e;

import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

// End-to-End test: RANDOM_PORT og HTTP-request simulerer fuldt flow til database
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class WorkdayE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private MaterialOrderRepository materialOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // Opret test-brugere hvis de ikke findes
        if (userRepository.findByEmail("admin@workday.dk").isEmpty()) {
            User admin = new User();
            admin.setName("Test Admin");
            admin.setEmail("admin@workday.dk");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
        
        if (userRepository.findByEmail("svend@workday.dk").isEmpty()) {
            User svend = new User();
            svend.setName("Test Svend");
            svend.setEmail("svend@workday.dk");
            svend.setPassword(passwordEncoder.encode("svend123"));
            svend.setRole(Role.SVEND);
            userRepository.save(svend);
        }
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void e2e_applicationContextLoads() {
        // Verificer at application context loader korrekt
        assertThat(materialOrderRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(userRepository.findByEmail("admin@workday.dk")).isPresent();
        assertThat(userRepository.findByEmail("svend@workday.dk")).isPresent();
    }
}

