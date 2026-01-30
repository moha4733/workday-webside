package dk.tommer.workday.controller.admin;

import dk.tommer.workday.Service.EmployeeTimeService;
import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.SickLeave;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/sick-leave")
public class AdminSickLeaveController {

    @Autowired
    private EmployeeTimeService employeeTimeService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listSickLeaves(Model model,
                                 @RequestParam(required = false) Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow();
        
        List<SickLeave> sickLeaves;
        
        if (userId != null) {
            sickLeaves = employeeTimeService.getSickLeavesByUser(userId);
        } else {
            sickLeaves = employeeTimeService.getAllSickLeavesForEmployees();
        }
        
        // Get all employees (SVEND and LÆRLING) for filter dropdown
        List<User> employees = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.SVEND || user.getRole() == Role.LÆRLING)
                .collect(Collectors.toList());
        
        model.addAttribute("userName", currentUser.getName());
        model.addAttribute("profilePhotoPath", currentUser.getProfilePhotoPath());
        model.addAttribute("sickLeaves", sickLeaves);
        model.addAttribute("allEmployees", employees);
        model.addAttribute("selectedUserId", userId);
        
        return "admin/sick-leave";
    }
}
