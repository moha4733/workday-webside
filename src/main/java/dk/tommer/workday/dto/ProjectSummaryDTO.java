package dk.tommer.workday.dto;

public class ProjectSummaryDTO {
    private Long id;
    private String name;
    private String address;
    private String description;
    private java.time.LocalTime startTime;
    public ProjectSummaryDTO() {}
    public ProjectSummaryDTO(Long id, String name, String address, String description, java.time.LocalTime startTime) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.startTime = startTime;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public java.time.LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(java.time.LocalTime startTime) {
        this.startTime = startTime;
    }
}
