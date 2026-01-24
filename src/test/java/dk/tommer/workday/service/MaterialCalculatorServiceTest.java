package dk.tommer.workday.service;

import dk.tommer.workday.dto.CalculationResultDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Unit tests: tester service-laget isoleret med JUnit 5 (AAA-pattern)
class MaterialCalculatorServiceTest {
    private final MaterialCalculatorService service = new MaterialCalculatorService();

    @Test
    void calculateFlooring_defaultWaste_and_packageRounding() {
        // Arrange
        double length = 4.0, width = 3.0; // net = 12.0
        Double wastePercentage = null;     // default 10%
        Double packageSize = 5.0;
        // Act
        CalculationResultDTO dto = service.calculateFlooring(length, width, wastePercentage, packageSize);
        // Assert
        assertEquals("floor", dto.getType());
        assertEquals(12.0, dto.getNetArea());
        assertEquals(1.21, dto.getWasteAmount());
        assertEquals(13.2, dto.getGrossArea());
        assertEquals(3, dto.getRecommendedOrderQuantity()); // ceil(13.2/5)=3
    }

    @Test
    void calculateFlooring_edge_zeroPackage_noRecommendation() {
        CalculationResultDTO dto = service.calculateFlooring(2.0, 2.0, 10.0, 0.0);
        assertNull(dto.getRecommendedOrderQuantity());
    }

    @Test
    void calculateWindowTrim_basic() {
        // 2*(h+w)*count * (1+wp)
        CalculationResultDTO dto = service.calculateWindowTrim(1.0, 0.5, 3, 10.0);
        assertEquals("windows", dto.getType());
        assertEquals(9.9, dto.getLinearMeters()); // perWindow=3.0; total=9.0; gross=9.9
    }

    @Test
    void calculateInsulation_boardsCalculated() {
        CalculationResultDTO dto = service.calculateInsulation(3.0, 2.0); // area=6.0
        assertEquals("insulation", dto.getType());
        assertEquals(6.0, dto.getInsulationArea());
        assertEquals(3, dto.getBoardCount()); // 6/2.88=2.08 -> ceil=3
    }

    @Test
    void calculateBattens_spacing_to_linearMeters() {
        CalculationResultDTO dto = service.calculateBattens(30.0, 30.0);
        assertEquals("battens", dto.getType());
        assertEquals(100.0, dto.getLinearMeters()); // spacing 0.3m -> 30/0.3 = 100
    }

    @Test
    void calculateBattens_zeroSpacing_returnsZero() {
        CalculationResultDTO dto = service.calculateBattens(30.0, 0.0);
        assertEquals(0.0, dto.getLinearMeters());
    }
}
