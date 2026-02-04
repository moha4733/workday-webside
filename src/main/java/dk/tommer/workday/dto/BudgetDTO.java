package dk.tommer.workday.dto;

import java.math.BigDecimal;
import dk.tommer.workday.entity.ProjectStatus;

public class BudgetDTO {
    private Long projectId;
    private String projectName;
    private ProjectStatus projectStatus;
    private BigDecimal totalBudget;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal totalSpent;
    private BigDecimal remainingBalance;
    private double spentPercentage;

    public BudgetDTO(Long projectId, String projectName, ProjectStatus projectStatus, BigDecimal totalBudget, BigDecimal materialCost, BigDecimal laborCost, BigDecimal totalSpent, BigDecimal remainingBalance, double spentPercentage) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectStatus = projectStatus;
        this.totalBudget = totalBudget != null ? totalBudget : BigDecimal.ZERO;
        this.materialCost = materialCost;
        this.laborCost = laborCost;
        this.totalSpent = totalSpent;
        this.remainingBalance = remainingBalance;
        this.spentPercentage = spentPercentage;
    }

    // Getters and Setters
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public ProjectStatus getProjectStatus() { return projectStatus; }
    public void setProjectStatus(ProjectStatus projectStatus) { this.projectStatus = projectStatus; }

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
