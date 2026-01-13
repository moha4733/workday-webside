package dk.tommer.workday.service;

import dk.tommer.workday.dto.CalculationResultDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MaterialCalculatorService {
    public CalculationResultDTO calculateFlooring(double length, double width, Double wastePercentage, Double packageSize) {
        double wp = wastePercentage != null ? wastePercentage : 10.0;
        double net = length * width;
        double waste = net * (wp / 100.0);
        double gross = net + waste;
        BigDecimal netRounded = BigDecimal.valueOf(net).setScale(2, RoundingMode.UP);
        BigDecimal wasteRounded = BigDecimal.valueOf(waste).setScale(2, RoundingMode.UP);
        BigDecimal grossRounded = BigDecimal.valueOf(gross).setScale(2, RoundingMode.UP);
        CalculationResultDTO dto = new CalculationResultDTO();
        dto.setType("floor");
        dto.setLength(length);
        dto.setWidth(width);
        dto.setWastePercentage(wp);
        dto.setNetArea(netRounded.doubleValue());
        dto.setWasteAmount(wasteRounded.doubleValue());
        dto.setGrossArea(grossRounded.doubleValue());
        if (packageSize != null && packageSize > 0) {
            int qty = (int) Math.ceil(grossRounded.doubleValue() / packageSize);
            dto.setRecommendedOrderQuantity(qty);
        }
        return dto;
    }

    public CalculationResultDTO calculateWindowTrim(double height, double width, int count, Double wastePercentage) {
        double wp = wastePercentage != null ? wastePercentage : 10.0;
        double perWindow = 2 * (height + width);
        double total = perWindow * count;
        double gross = total * (1 + wp / 100.0);
        BigDecimal grossRounded = BigDecimal.valueOf(gross).setScale(2, RoundingMode.UP);
        CalculationResultDTO dto = new CalculationResultDTO();
        dto.setType("windows");
        dto.setWastePercentage(wp);
        dto.setLinearMeters(grossRounded.doubleValue());
        return dto;
    }

    public CalculationResultDTO calculateInsulation(double wallLength, double wallHeight) {
        double area = wallLength * wallHeight;
        BigDecimal areaRounded = BigDecimal.valueOf(area).setScale(2, RoundingMode.UP);
        double boardArea = 1.2 * 2.4; // 2.88 m2
        int boards = (int) Math.ceil(area / boardArea);
        CalculationResultDTO dto = new CalculationResultDTO();
        dto.setType("insulation");
        dto.setInsulationArea(areaRounded.doubleValue());
        dto.setBoardCount(boards);
        return dto;
    }

    public CalculationResultDTO calculateBattens(double totalArea, double spacingCm) {
        double spacingM = spacingCm / 100.0;
        double linear = spacingM > 0 ? totalArea / spacingM : 0.0;
        BigDecimal linearRounded = BigDecimal.valueOf(linear).setScale(2, RoundingMode.UP);
        CalculationResultDTO dto = new CalculationResultDTO();
        dto.setType("battens");
        dto.setLinearMeters(linearRounded.doubleValue());
        return dto;
    }
}
