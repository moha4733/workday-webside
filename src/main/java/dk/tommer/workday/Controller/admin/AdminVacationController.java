package dk.tommer.workday.controller.admin;

import dk.tommer.workday.Service.EmployeeTimeService;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.VacationRequest;
import dk.tommer.workday.entity.VacationStatus;
import dk.tommer.workday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/vacation")
public class AdminVacationController {

    @Autowired
    private EmployeeTimeService employeeTimeService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listVacationRequests(Model model,
                                       @RequestParam(required = false) Long userId,
                                       @RequestParam(required = false) String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow();
        
        List<VacationRequest> requests;
        
        if (userId != null) {
            requests = employeeTimeService.getVacationRequestsByUser(userId);
        } else if (status != null && !status.isEmpty()) {
            try {
                VacationStatus vacationStatus = VacationStatus.valueOf(status.toUpperCase());
                requests = employeeTimeService.getVacationRequestsByStatus(vacationStatus);
            } catch (IllegalArgumentException e) {
                requests = employeeTimeService.getAllVacationRequests();
            }
        } else {
            requests = employeeTimeService.getAllVacationRequests();
        }
        
        List<User> allUsers = userRepository.findAll();
        
        model.addAttribute("userName", currentUser.getName());
        model.addAttribute("profilePhotoPath", currentUser.getProfilePhotoPath());
        model.addAttribute("vacationRequests", requests);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", VacationStatus.values());
        
        return "admin/vacation-requests";
    }

    @PostMapping("/{id}/approve")
    public String approveVacationRequest(@PathVariable Long id,
                                        @RequestParam(required = false) String adminComment,
                                        RedirectAttributes redirectAttributes) {
        try {
            employeeTimeService.updateVacationRequestStatus(id, VacationStatus.GODKENDT, adminComment);
            redirectAttributes.addFlashAttribute("success", "Ferieønsker godkendt!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved godkendelse: " + e.getMessage());
        }
        return "redirect:/admin/vacation";
    }

    @PostMapping("/{id}/reject")
    public String rejectVacationRequest(@PathVariable Long id,
                                       @RequestParam(required = false) String adminComment,
                                       RedirectAttributes redirectAttributes) {
        try {
            employeeTimeService.updateVacationRequestStatus(id, VacationStatus.AFVIST, adminComment);
            redirectAttributes.addFlashAttribute("success", "Ferieønsker afvist!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved afvisning: " + e.getMessage());
        }
        return "redirect:/admin/vacation";
    }

    @PostMapping("/{id}/pending")
    public String setPendingVacationRequest(@PathVariable Long id,
                                           RedirectAttributes redirectAttributes) {
        try {
            employeeTimeService.updateVacationRequestStatus(id, VacationStatus.AFVENTER, null);
            redirectAttributes.addFlashAttribute("success", "Ferieønsker sat til afventer!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved opdatering: " + e.getMessage());
        }
        return "redirect:/admin/vacation";
    }
}
