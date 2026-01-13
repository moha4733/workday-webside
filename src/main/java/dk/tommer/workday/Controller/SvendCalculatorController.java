package dk.tommer.workday.controller;

import dk.tommer.workday.dto.CalculationResultDTO;
import dk.tommer.workday.entity.MaterialOrder;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.service.MaterialCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/svend")
public class SvendCalculatorController {
    @Autowired
    private MaterialCalculatorService calculatorService;
    @Autowired
    private MaterialOrderRepository materialOrderRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/calculate/flooring")
    public CalculationResultDTO calculateFlooring(@RequestParam double length,
                                                  @RequestParam double width,
                                                  @RequestParam(required = false, defaultValue = "10") double wastePercentage,
                                                  @RequestParam(required = false) Double packageSize) {
        return calculatorService.calculateFlooring(length, width, wastePercentage, packageSize);
    }

    @PostMapping("/material-order")
    public void createMaterialOrder(@RequestParam double grossArea) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        MaterialOrder order = new MaterialOrder();
        order.setUser(userRepository.findByEmail(email).orElseThrow());
        order.setDescription("Gulvmateriale bestilling: " + grossArea + " m2");
        materialOrderRepository.save(order);
    }
}
