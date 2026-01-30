package dk.tommer.workday.controller.admin;

import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.Service.EmployeeService;
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
    private EmployeeService employeeService;

    @GetMapping
    public String listEmployees(@RequestParam(required = false) String search, Model model) {
        List<User> employees = employeeService.getAllEmployeesWithWorkHours();
        
        // Filtrer efter søgning hvis angivet
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase().trim();
            employees = employees.stream()
                    .filter(user -> (user.getName() != null && user.getName().toLowerCase().contains(searchLower)) ||
                               (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchLower)))
                    .toList();
        }
        
        model.addAttribute("employees", employees);
        model.addAttribute("employeeService", employeeService);
        model.addAttribute("searchQuery", search);
        return "admin/employees";
    }

    @GetMapping("/{id}/projects")
    public String viewEmployeeProjects(@PathVariable Long id,
                                       @RequestParam(required = false) String startDate,
                                       @RequestParam(required = false) String endDate,
                                       Model model) {
        User employee = employeeService.getEmployeeById(id);
        List<Project> projects = employeeService.getAllProjectsForEmployee(id);
        
        // Filtrer efter dato hvis angivet
        if (startDate != null && !startDate.trim().isEmpty()) {
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            projects = projects.stream()
                    .filter(p -> p.getStartDate() != null && !p.getStartDate().isBefore(start))
                    .toList();
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            projects = projects.stream()
                    .filter(p -> p.getStartDate() != null && !p.getStartDate().isAfter(end))
                    .toList();
        }
        
        model.addAttribute("employee", employee);
        model.addAttribute("projects", projects);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/employee-projects";
    }

    @GetMapping("/{id}/edit-hours")
    public String showEditHoursForm(@PathVariable Long id, Model model) {
        User employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        return "admin/edit-employee-hours";
    }

    @PostMapping("/{id}/edit-hours")
    public String updateWorkHours(@PathVariable Long id,
                                  @RequestParam Double workHours,
                                  RedirectAttributes redirectAttributes) {
        employeeService.updateEmployeeWorkHours(id, workHours);
        redirectAttributes.addFlashAttribute("success", "Arbejdstimer opdateret succesfuldt!");
        return "redirect:/admin/employees";
    }
}

