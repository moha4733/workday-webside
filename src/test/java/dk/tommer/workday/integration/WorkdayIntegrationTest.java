package dk.tommer.workday.integration;

import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Integration tests: kører fuld application context med H2 og tester lag-samspil
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WorkdayIntegrationTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MaterialOrderRepository materialOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        
        // Opret test-bruger hvis den ikke findes
        if (userRepository.findByEmail("svend@workday.dk").isEmpty()) {
            User svend = new User();
            svend.setName("Test Svend");
            svend.setEmail("svend@workday.dk");
            svend.setPassword(passwordEncoder.encode("svend123"));
            svend.setRole(Role.SVEND);
            userRepository.save(svend);
        }
    }
    
    @Test
    @WithMockUser(username = "svend@workday.dk", roles = "SVEND")
    void createMaterialOrder_flow_savesToDatabase() throws Exception {
        long before = materialOrderRepository.count();
        mockMvc.perform(post("/api/svend/material-order")
                        .param("grossArea", "20.0")
                        .param("description", "Test material order"))
                .andExpect(status().isOk());
        long after = materialOrderRepository.count();
        assertThat(after).isEqualTo(before + 1);
    }
}
