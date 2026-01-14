package dk.tommer.workday.controller;

import dk.tommer.workday.dto.CalculationResultDTO;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.service.MaterialCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Web slice tests: standalone MockMvc med Mockito for isolation af controller-laget
class SvendCalculatorControllerWebTest {

    private MockMvc mockMvc;
    private MaterialCalculatorService calculatorService;
    private MaterialOrderRepository materialOrderRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        calculatorService = Mockito.mock(MaterialCalculatorService.class);
        materialOrderRepository = Mockito.mock(MaterialOrderRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        SvendCalculatorController controller = new SvendCalculatorController();
        ReflectionTestUtils.setField(controller, "calculatorService", calculatorService);
        ReflectionTestUtils.setField(controller, "materialOrderRepository", materialOrderRepository);
        ReflectionTestUtils.setField(controller, "userRepository", userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void calculateFlooring_returnsJson() throws Exception {
        CalculationResultDTO dto = new CalculationResultDTO();
        dto.setType("floor");
        dto.setGrossArea(13.2);
        when(calculatorService.calculateFlooring(4.0, 3.0, 10.0, null)).thenReturn(dto);

        mockMvc.perform(get("/api/svend/calculate/flooring")
                        .param("length", "4.0")
                        .param("width", "3.0")
                        .param("wastePercentage", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("floor"))
                .andExpect(jsonPath("$.grossArea").value(13.2));
    }

    @Test
    void createMaterialOrder_savesOrder() throws Exception {
        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn("svend@workday.dk");
        SecurityContext sc = Mockito.mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        User u = new User();
        u.setEmail("svend@workday.dk");
        when(userRepository.findByEmail("svend@workday.dk")).thenReturn(Optional.of(u));

        mockMvc.perform(post("/api/svend/material-order")
                        .param("grossArea", "15.0"))
                .andExpect(status().isOk());

        verify(materialOrderRepository, times(1)).save(any());
    }
}
