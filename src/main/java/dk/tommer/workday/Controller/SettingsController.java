package dk.tommer.workday.Controller;

import dk.tommer.workday.Entity.Company;
import dk.tommer.workday.Entity.User;
import dk.tommer.workday.Entity.WorkType;
import dk.tommer.workday.Repo.CompanyRepository;
import dk.tommer.workday.Repo.UserRepository;
import dk.tommer.workday.Repo.WorkTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/settings")
public class SettingsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private WorkTypeRepository workTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showSettings(Model model) {
        // Hent current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Hent eller opret company info
        Company company = companyRepository.findFirstByOrderByIdAsc()
                .orElse(new Company());

        // Hent alle work types
        List<WorkType> workTypes = workTypeRepository.findAllByOrderByNameAsc();

        model.addAttribute("user", currentUser);
        model.addAttribute("company", company);
        model.addAttribute("workTypes", workTypes);
        return "settings";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name,
                               @RequestParam String email,
                               RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = auth.getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tjek om email allerede eksisterer (hvis det er en anden email)
        if (!email.equals(currentEmail)) {
            if (userRepository.findByEmail(email).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Email er allerede i brug!");
                return "redirect:/admin/settings";
            }
        }

        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Profil opdateret succesfuldt!");
        return "redirect:/admin/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tjek om gamle password er korrekt
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Gammelt password er forkert!");
            return "redirect:/admin/settings";
        }

        // Tjek om nye passwords matcher
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Nye passwords matcher ikke!");
            return "redirect:/admin/settings";
        }

        // Tjek minimum længde
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Nyt password skal være mindst 6 tegn!");
            return "redirect:/admin/settings";
        }

        // Opdater password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("success", "Password opdateret succesfuldt!");
        return "redirect:/admin/settings";
    }

    @PostMapping("/company")
    public String updateCompany(@RequestParam(required = false) String companyName,
                               @RequestParam(required = false) String cvrNumber,
                               @RequestParam(required = false) Double standardHourlyRate,
                               RedirectAttributes redirectAttributes) {
        Company company = companyRepository.findFirstByOrderByIdAsc()
                .orElse(new Company());

        company.setCompanyName(companyName);
        company.setCvrNumber(cvrNumber);
        company.setStandardHourlyRate(standardHourlyRate);
        companyRepository.save(company);

        redirectAttributes.addFlashAttribute("success", "Virksomhedsinfo opdateret succesfuldt!");
        return "redirect:/admin/settings";
    }

    @PostMapping("/work-types/add")
    public String addWorkType(@RequestParam String name,
                             @RequestParam(required = false) String description,
                             RedirectAttributes redirectAttributes) {
        if (workTypeRepository.existsByName(name)) {
            redirectAttributes.addFlashAttribute("error", "Arbejdstype med dette navn eksisterer allerede!");
            return "redirect:/admin/settings";
        }

        WorkType workType = new WorkType(name, description);
        workTypeRepository.save(workType);

        redirectAttributes.addFlashAttribute("success", "Arbejdstype tilføjet succesfuldt!");
        return "redirect:/admin/settings";
    }

    @PostMapping("/work-types/{id}/delete")
    public String deleteWorkType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        workTypeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Arbejdstype slettet succesfuldt!");
        return "redirect:/admin/settings";
    }
}

