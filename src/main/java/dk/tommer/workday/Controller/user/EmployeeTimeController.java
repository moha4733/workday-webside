package dk.tommer.workday.controller.user;

import dk.tommer.workday.Service.EmployeeTimeService;
import dk.tommer.workday.entity.*;
import dk.tommer.workday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/svend/time")
public class EmployeeTimeController {

    @Autowired
    private EmployeeTimeService employeeTimeService;

    @Autowired
    private UserRepository userRepository;

    // Sick Leave
    @GetMapping("/sick-leave")
    public String showSickLeavePage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        List<SickLeave> sickLeaves = employeeTimeService.getSickLeavesByUser(user.getId());
        model.addAttribute("userName", user.getName());
        model.addAttribute("sickLeaves", sickLeaves);
        return "user/sick-leave";
    }

    @PostMapping("/sick-leave")
    public String createSickLeave(@RequestParam LocalDate startDate,
                                  @RequestParam LocalDate endDate,
                                  @RequestParam(required = false) String comment,
                                  RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        try {
            employeeTimeService.createSickLeave(user.getId(), startDate, endDate, comment);
            redirectAttributes.addFlashAttribute("success", "Sygemelding registreret!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved registrering: " + e.getMessage());
        }
        return "redirect:/svend/time/sick-leave";
    }

    @PostMapping("/sick-leave/{id}/delete")
    public String deleteSickLeave(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        try {
            employeeTimeService.deleteSickLeave(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Sygemelding slettet!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved sletning: " + e.getMessage());
        }
        return "redirect:/svend/time/sick-leave";
    }

    // Vacation Requests
    @GetMapping("/vacation")
    public String showVacationPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        List<VacationRequest> requests = employeeTimeService.getVacationRequestsByUser(user.getId());
        model.addAttribute("userName", user.getName());
        model.addAttribute("vacationRequests", requests);
        return "user/vacation";
    }

    @PostMapping("/vacation")
    public String createVacationRequest(@RequestParam LocalDate startDate,
                                        @RequestParam LocalDate endDate,
                                        @RequestParam(required = false) String comment,
                                        RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        try {
            employeeTimeService.createVacationRequest(user.getId(), startDate, endDate, comment);
            redirectAttributes.addFlashAttribute("success", "Ferieønsker indsendt!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved indsendelse: " + e.getMessage());
        }
        return "redirect:/svend/time/vacation";
    }

    @PostMapping("/vacation/{id}/delete")
    public String deleteVacationRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        try {
            employeeTimeService.deleteVacationRequest(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Ferieønsker slettet!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved sletning: " + e.getMessage());
        }
        return "redirect:/svend/time/vacation";
    }

    // Travel Allowance
    @GetMapping("/travel")
    public String showTravelPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        List<TravelAllowance> allowances = employeeTimeService.getTravelAllowancesByUser(user.getId());
        model.addAttribute("userName", user.getName());
        model.addAttribute("travelAllowances", allowances);
        return "user/travel";
    }

    @PostMapping("/travel")
    public String createTravelAllowance(@RequestParam LocalDate date,
                                       @RequestParam(required = false) Double kilometers,
                                       @RequestParam(required = false) Double amount,
                                       @RequestParam(required = false) String comment,
                                       RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        if (kilometers == null && amount == null) {
            redirectAttributes.addFlashAttribute("error", "Angiv enten kilometer eller beløb");
            return "redirect:/svend/time/travel";
        }
        
        try {
            employeeTimeService.createTravelAllowance(user.getId(), date, kilometers, amount, comment);
            redirectAttributes.addFlashAttribute("success", "Kørseltillæg registreret!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved registrering: " + e.getMessage());
        }
        return "redirect:/svend/time/travel";
    }

    @PostMapping("/travel/{id}/delete")
    public String deleteTravelAllowance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        try {
            employeeTimeService.deleteTravelAllowance(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Kørseltillæg slettet!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved sletning: " + e.getMessage());
        }
        return "redirect:/svend/time/travel";
    }

    // Lunch Break
    @GetMapping("/lunch")
    public String showLunchPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        List<LunchBreak> lunchBreaks = employeeTimeService.getLunchBreaksByUser(user.getId());
        model.addAttribute("userName", user.getName());
        model.addAttribute("lunchBreaks", lunchBreaks);
        return "user/lunch";
    }

    @PostMapping("/lunch")
    public String createLunchBreak(@RequestParam LocalDate date,
                                   @RequestParam Integer durationMinutes,
                                   RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        try {
            employeeTimeService.createLunchBreak(user.getId(), date, durationMinutes);
            redirectAttributes.addFlashAttribute("success", "Frokostpause registreret!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved registrering: " + e.getMessage());
        }
        return "redirect:/svend/time/lunch";
    }

    @PostMapping("/lunch/{id}/delete")
    public String deleteLunchBreak(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        try {
            employeeTimeService.deleteLunchBreak(id, user.getId());
            redirectAttributes.addFlashAttribute("success", "Frokostpause slettet!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Fejl ved sletning: " + e.getMessage());
        }
        return "redirect:/svend/time/lunch";
    }
}
