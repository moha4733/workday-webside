package dk.tommer.workday.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "work_logs")
public class WorkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private Double hours;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkLogStatus status = WorkLogStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String adminComment;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Double getHours() {
        return hours;
    }
    
    public void setHours(Double hours) {
        this.hours = hours;
    }
    
    public WorkLogStatus getStatus() {
        return status;
    }
    
    public void setStatus(WorkLogStatus status) {
        this.status = status;
    }
    
    public String getAdminComment() {
        return adminComment;
    }
    
    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
}
