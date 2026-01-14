package dk.tommer.workday.integration;

import dk.tommer.workday.repository.MaterialOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Integration tests: kører fuld application context med H2 og tester lag-samspil
@SpringBootTest
class WorkdayIntegrationTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MaterialOrderRepository materialOrderRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    @Test
    @WithMockUser(username = "svend@workday.dk")
    void createMaterialOrder_flow_savesToDatabase() throws Exception {
        long before = materialOrderRepository.count();
        mockMvc.perform(post("/api/svend/material-order").param("grossArea", "20.0"))
                .andExpect(status().isOk());
        long after = materialOrderRepository.count();
        assertThat(after).isEqualTo(before + 1);
    }
}
