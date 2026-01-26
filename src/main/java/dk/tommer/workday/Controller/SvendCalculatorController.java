package dk.tommer.workday.Controller;

import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.service.MaterialCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/svend")
public class SvendCalculatorController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MaterialCalculatorService calculatorService;

    @GetMapping("/calculator")
    public String calculator(Model model,
                             @RequestParam(required = false) String type,
                             @RequestParam(required = false) Long projectId,
                             @RequestParam(required = false) Double length,
                             @RequestParam(required = false) Double width,
                             @RequestParam(required = false) Double wastePercentage,
                             @RequestParam(required = false) Double packageSize,
                             @RequestParam(required = false) Double height,
                             @RequestParam(required = false) Integer windowCount,
                             @RequestParam(required = false) Double wallLength,
                             @RequestParam(required = false) Double wallHeight,
                             @RequestParam(required = false) Double battensArea,
                             @RequestParam(required = false) Double battensSpacingCm) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("userName", user.getName());
        if (projectId != null) {
            Project project = projectRepository.findById(projectId).orElse(null);
            if (project != null && project.getAssignedUser() != null && project.getAssignedUser().getId() == user.getId()) {
                model.addAttribute("project", project);
                model.addAttribute("projectId", projectId);
            } else {
                model.addAttribute("projectId", projectId);
            }
        }
        if ("floor".equalsIgnoreCase(type) && length != null && width != null && length > 0 && width > 0) {
            var calc = calculatorService.calculateFlooring(length, width, wastePercentage, packageSize);
            model.addAttribute("calc", calc);
        } else if ("windows".equalsIgnoreCase(type) && height != null && width != null && windowCount != null
                && height > 0 && width > 0 && windowCount > 0) {
            var calc = calculatorService.calculateWindowTrim(height, width, windowCount, wastePercentage);
            model.addAttribute("calc", calc);
        } else if ("insulation".equalsIgnoreCase(type) && wallLength != null && wallHeight != null
                && wallLength > 0 && wallHeight > 0) {
            var calc = calculatorService.calculateInsulation(wallLength, wallHeight);
            model.addAttribute("calc", calc);
        } else if ("battens".equalsIgnoreCase(type) && battensArea != null && battensSpacingCm != null
                && battensArea > 0 && battensSpacingCm > 0) {
            var calc = calculatorService.calculateBattens(battensArea, battensSpacingCm);
            model.addAttribute("calc", calc);
        } else if (type != null) {
            model.addAttribute("calcError", "Udfyld venligst alle felter med gyldige værdier for: " + type);
        }
        model.addAttribute("type", type);
        return "svend-calculator";
    }
}
