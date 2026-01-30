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
    public String listEmployees(Model model) {
        List<User> employees = employeeService.getAllEmployeesWithWorkHours();
        model.addAttribute("employees", employees);
        return "admin/employees";
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

