package dk.tommer.workday.dto;

import dk.tommer.workday.entity.MaterialStatus;
import java.time.LocalDateTime;

public class MaterialOrderDTO {
    private Long id;
    private String description;
    private MaterialStatus status;
    private LocalDateTime createdAt;
    public MaterialOrderDTO() {}
    public MaterialOrderDTO(Long id, String description, MaterialStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public MaterialStatus getStatus() {
        return status;
    }
    public void setStatus(MaterialStatus status) {
        this.status = status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
