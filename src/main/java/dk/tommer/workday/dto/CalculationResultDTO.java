package dk.tommer.workday.dto;

public class CalculationResultDTO {
    private String type;
    private double length;
    private double width;
    private double wastePercentage;
    private double netArea;
    private double grossArea;
    private double wasteAmount;
    private Integer recommendedOrderQuantity;
    private Double linearMeters;
    private Double insulationArea;
    private Integer boardCount;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    public double getWastePercentage() { return wastePercentage; }
    public void setWastePercentage(double wastePercentage) { this.wastePercentage = wastePercentage; }
    public double getNetArea() { return netArea; }
    public void setNetArea(double netArea) { this.netArea = netArea; }
    public double getGrossArea() { return grossArea; }
    public void setGrossArea(double grossArea) { this.grossArea = grossArea; }
    public double getWasteAmount() { return wasteAmount; }
    public void setWasteAmount(double wasteAmount) { this.wasteAmount = wasteAmount; }
    public Integer getRecommendedOrderQuantity() { return recommendedOrderQuantity; }
    public void setRecommendedOrderQuantity(Integer recommendedOrderQuantity) { this.recommendedOrderQuantity = recommendedOrderQuantity; }
    public Double getLinearMeters() { return linearMeters; }
    public void setLinearMeters(Double linearMeters) { this.linearMeters = linearMeters; }
    public Double getInsulationArea() { return insulationArea; }
    public void setInsulationArea(Double insulationArea) { this.insulationArea = insulationArea; }
    public Integer getBoardCount() { return boardCount; }
    public void setBoardCount(Integer boardCount) { this.boardCount = boardCount; }
}
