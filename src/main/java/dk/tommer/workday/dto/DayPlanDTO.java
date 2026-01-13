package dk.tommer.workday.dto;

import java.time.LocalDate;

public class DayPlanDTO {
    private LocalDate date;
    private Long projectId;
    private String projectName;
    private String address;
    private String description;
    public DayPlanDTO() {}
    public DayPlanDTO(LocalDate date, Long projectId, String projectName, String address, String description) {
        this.date = date;
        this.projectId = projectId;
        this.projectName = projectName;
        this.address = address;
        this.description = description;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
