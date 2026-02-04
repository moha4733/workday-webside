package dk.tommer.workday.dto;

import java.math.BigDecimal;

public class BudgetDTO {
    private String projectName;
    private BigDecimal totalBudget;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal totalSpent;
    private BigDecimal remainingBalance;
    private double spentPercentage;

    public BudgetDTO(String projectName, BigDecimal totalBudget, BigDecimal materialCost, BigDecimal laborCost, BigDecimal totalSpent, BigDecimal remainingBalance, double spentPercentage) {
        this.projectName = projectName;
        this.totalBudget = totalBudget != null ? totalBudget : BigDecimal.ZERO;
        this.materialCost = materialCost;
        this.laborCost = laborCost;
        this.totalSpent = totalSpent;
        this.remainingBalance = remainingBalance;
        this.spentPercentage = spentPercentage;
    }

    // Getters and Setters
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public BigDecimal getTotalBudget() { return totalBudget; }
    public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }

    public BigDecimal getMaterialCost() { return materialCost; }
    public void setMaterialCost(BigDecimal materialCost) { this.materialCost = materialCost; }

    public BigDecimal getLaborCost() { return laborCost; }
    public void setLaborCost(BigDecimal laborCost) { this.laborCost = laborCost; }

    public BigDecimal getTotalSpent() { return totalSpent; }
    public void setTotalSpent(BigDecimal totalSpent) { this.totalSpent = totalSpent; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }

    public double getSpentPercentage() { return spentPercentage; }
    public void setSpentPercentage(double spentPercentage) { this.spentPercentage = spentPercentage; }
}
