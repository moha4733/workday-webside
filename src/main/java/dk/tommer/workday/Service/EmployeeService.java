package dk.tommer.workday.Service;

import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.WorkLog;
import dk.tommer.workday.entity.WorkLogStatus;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.repository.WorkLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkLogRepository workLogRepository;

    public List<User> getAllEmployeesWithWorkHours() {
        // Hent alle brugere med SVEND eller LÆRLING rolle (medarbejdere)
        List<User> employees = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.SVEND || user.getRole() == Role.LÆRLING)
                .toList();

        // Hent alle godkendte worklogs
        List<WorkLog> approvedWorkLogs = workLogRepository.findByStatus(WorkLogStatus.APPROVED);
        
        // Gruppér worklogs efter bruger
        Map<Long, List<WorkLog>> workLogsByUser = approvedWorkLogs.stream()
                .collect(Collectors.groupingBy(wl -> wl.getUser().getId()));

        // For hver medarbejder, beregn faktiske timer og pause
        for (User employee : employees) {
            List<WorkLog> userWorkLogs = workLogsByUser.getOrDefault(employee.getId(), List.of());
            
            // Beregn total arbejdstimer
            double totalHours = userWorkLogs.stream()
                    .mapToDouble(WorkLog::getHours)
                    .sum();
            
            // Gem total timer i workHours feltet (kan bruges til visning)
            employee.setWorkHours(totalHours);
        }
        return employees;
    }
    
    public double getTotalWorkHours(Long userId) {
        List<WorkLog> approvedWorkLogs = workLogRepository.findByStatus(WorkLogStatus.APPROVED);
        return approvedWorkLogs.stream()
                .filter(wl -> wl.getUser().getId().equals(userId))
                .mapToDouble(WorkLog::getHours)
                .sum();
    }
    
    public double getTotalBreakHours(Long userId) {
        List<WorkLog> approvedWorkLogs = workLogRepository.findByStatus(WorkLogStatus.APPROVED);
        return approvedWorkLogs.stream()
                .filter(wl -> wl.getUser().getId().equals(userId))
                .filter(wl -> wl.getBreakMinutes() != null && wl.getBreakMinutes() > 0)
                .mapToDouble(wl -> wl.getBreakMinutes() / 60.0)
                .sum();
    }
    
    public int getTotalBreakMinutes(Long userId) {
        List<WorkLog> approvedWorkLogs = workLogRepository.findByStatus(WorkLogStatus.APPROVED);
        return approvedWorkLogs.stream()
                .filter(wl -> wl.getUser().getId().equals(userId))
                .filter(wl -> wl.getBreakMinutes() != null && wl.getBreakMinutes() > 0)
                .mapToInt(WorkLog::getBreakMinutes)
                .sum();
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
