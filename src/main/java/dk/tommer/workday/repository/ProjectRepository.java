package dk.tommer.workday.repository;

import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByAssignedUser_Id(Long userId);
    List<Project> findByStatus(ProjectStatus status);
    List<Project> findTop5ByOrderByIdDesc();
}
