package dk.tommer.workday.controller.user;

import dk.tommer.workday.dto.DayPlanDTO;
import dk.tommer.workday.dto.MaterialOrderDTO;
import dk.tommer.workday.dto.ProjectSummaryDTO;
import dk.tommer.workday.entity.DayPlan;
import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.ProjectStatus;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.DayPlanRepository;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.repository.WorkLogRepository;
import dk.tommer.workday.Service.MaterialCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/svend")
public class SvendDashboardController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DayPlanRepository dayPlanRepository;
    @Autowired
    private MaterialOrderRepository materialOrderRepository;
    @Autowired
    private WorkLogRepository workLogRepository;
    @Autowired
    private MaterialCalculatorService calculatorService;
    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @RequestParam(required = false) Double length,
                            @RequestParam(required = false) Double width,
                            @RequestParam(required = false) Double wastePercentage,
                            @RequestParam(required = false) Double packageSize,
                            @RequestParam(required = false) Boolean showCalc) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Long userId = user.getId();
        LocalDate today = LocalDate.now();

        Optional<DayPlan> todayPlan = dayPlanRepository.findFirstByUser_IdAndDateOrderByDateAsc(userId, today);
        ProjectSummaryDTO currentProject = todayPlan
                .map(DayPlan::getProject)
                .map(p -> new ProjectSummaryDTO(p.getId(), p.getName(), p.getAddress(), p.getDescription(), p.getStartTime()))
                .orElseGet(() -> {
                    List<Project> assigned = projectRepository.findByAssignedUser_Id(userId);
                    Project inProgress = assigned.stream().filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS).findFirst().orElse(null);
                    Project fallback = inProgress != null ? inProgress : (assigned.isEmpty() ? null : assigned.get(0));
                    return fallback != null ? new ProjectSummaryDTO(fallback.getId(), fallback.getName(), fallback.getAddress(), fallback.getDescription(), fallback.getStartTime()) : null;
                });

        List<DayPlanDTO> calendarPreview = dayPlanRepository
                .findTop5ByUser_IdAndDateGreaterThanEqualOrderByDateAsc(userId, today)
                .stream()
                .map(dp -> {
                    Project p = dp.getProject();
                    return new DayPlanDTO(dp.getDate(), p != null ? p.getId() : null, p != null ? p.getName() : null,
                            p != null ? p.getAddress() : null, p != null ? p.getDescription() : null, p != null ? p.getStartTime() : null);
                })
                .collect(Collectors.toList());
        if (calendarPreview.isEmpty()) {
            List<DayPlanDTO> projectBased = projectRepository.findByAssignedUser_Id(userId).stream()
                    .filter(p -> p.getStartDate() != null && !p.getStartDate().isBefore(today))
                    .sorted((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
                    .limit(5)
                    .map(p -> new DayPlanDTO(p.getStartDate(), p.getId(), p.getName(), p.getAddress(), p.getDescription(), p.getStartTime()))
                    .collect(Collectors.toList());
            calendarPreview = projectBased;
        }

        List<MaterialOrderDTO> materialStatus = materialOrderRepository
                .findTop3ByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mo -> new MaterialOrderDTO(mo.getId(), mo.getDescription(), mo.getStatus(), mo.getCreatedAt()))
                .collect(Collectors.toList());
        String latestOrderStatus = "Ingen";
        if (!materialStatus.isEmpty()) {
            var st = materialStatus.get(0).getStatus();
            latestOrderStatus = (st != null) ? st.name() : "Ingen";
        }

        Double dailyTotalHours = workLogRepository.sumHoursByUserIdAndDate(userId, today);

        model.addAttribute("userName", user.getName());
        model.addAttribute("profilePhotoPath", user.getProfilePhotoPath());
        model.addAttribute("currentProject", currentProject);
        model.addAttribute("calendarPreview", calendarPreview);
        model.addAttribute("materialStatus", materialStatus);
        model.addAttribute("latestOrderStatus", latestOrderStatus);
        model.addAttribute("dailyTotalHours", dailyTotalHours != null ? dailyTotalHours : 0.0);
        model.addAttribute("showCalc", showCalc != null && showCalc);
        if (Boolean.TRUE.equals(showCalc) && length != null && width != null) {
            var calc = calculatorService.calculateFlooring(length, width, wastePercentage, packageSize);
            model.addAttribute("calc", calc);
        }
        List<Project> myProjects = projectRepository.findByAssignedUser_Id(userId);
        model.addAttribute("myProjects", myProjects);
        return "user/svend-dashboard";
    }
}
