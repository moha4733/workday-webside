package dk.tommer.workday.service;

import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<User> getAllEmployeesWithWorkHours() {
        // Hent alle brugere med SVEND rolle (medarbejdere)
        List<User> employees = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.SVEND)
                .toList();

        // For hver medarbejder, find deres tildelte projekter
        for (User employee : employees) {
            List<Project> assignedProjects = projectRepository.findByAssignedUser_Id(employee.getId());
            // Beregn total arbejdstimer baseret på tildelte projekter (simuleret)
            double totalHours = assignedProjects.size() * 8.0; // 8 timer per projekt
            employee.setWorkHours(totalHours);
        }
        return employees;
    }

    public User getEmployeeById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public void updateEmployeeWorkHours(Long id, Double workHours) {
        User employee = getEmployeeById(id);
        employee.setWorkHours(workHours);
        userRepository.save(employee);
    }
}
