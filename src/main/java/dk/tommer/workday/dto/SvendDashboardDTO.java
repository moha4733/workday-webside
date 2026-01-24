package dk.tommer.workday.dto;

import java.util.List;

public class SvendDashboardDTO {
    private ProjectSummaryDTO currentProject;
    private List<DayPlanDTO> calendarPreview;
    private List<MaterialOrderDTO> materialStatus;
    private Double dailyTotalHours;
    public ProjectSummaryDTO getCurrentProject() {
        return currentProject;
    }
    public void setCurrentProject(ProjectSummaryDTO currentProject) {
        this.currentProject = currentProject;
    }
    public List<DayPlanDTO> getCalendarPreview() {
        return calendarPreview;
    }
    public void setCalendarPreview(List<DayPlanDTO> calendarPreview) {
        this.calendarPreview = new ArrayList<>(calendarPreview);
    }
    public List<MaterialOrderDTO> getMaterialStatus() {
        return materialStatus;
    }
    public void setMaterialStatus(List<MaterialOrderDTO> materialStatus) {
        this.materialStatus = new ArrayList<>(materialStatus);
    }
    public Double getDailyTotalHours() {
        return dailyTotalHours;
    }
    public void setDailyTotalHours(Double dailyTotalHours) {
        this.dailyTotalHours = dailyTotalHours;
    }
}
