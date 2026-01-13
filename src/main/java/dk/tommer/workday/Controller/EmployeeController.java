package dk.tommer.workday.controller;

import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/employees")
public class EmployeeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping
    public String listEmployees(Model model) {
        // Hent alle brugere med USER rolle (medarbejdere)
        List<User> employees = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.USER)
                .toList();
        
        // For hver medarbejder, find deres tildelte projekter
        for (User employee : employees) {
            List<Project> assignedProjects = projectRepository.findByAssignedUser_Id(employee.getId());
            // Beregn total arbejdstimer baseret på tildelte projekter (simuleret)
            double totalHours = assignedProjects.size() * 8.0; // 8 timer per projekt
            employee.setWorkHours(totalHours);
        }
        
        model.addAttribute("employees", employees);
        return "employees";
    }

    @GetMapping("/{id}/edit-hours")
    public String showEditHoursForm(@PathVariable Long id, Model model) {
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        model.addAttribute("employee", employee);
        return "edit-employee-hours";
    }

    @PostMapping("/{id}/edit-hours")
    public String updateWorkHours(@PathVariable Long id,
                                  @RequestParam Double workHours,
                                  RedirectAttributes redirectAttributes) {
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employee.setWorkHours(workHours);
        userRepository.save(employee);
        redirectAttributes.addFlashAttribute("success", "Arbejdstimer opdateret succesfuldt!");
        return "redirect:/admin/employees";
    }
}

