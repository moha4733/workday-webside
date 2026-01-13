package dk.tommer.workday.controller;

import dk.tommer.workday.dto.*;
import dk.tommer.workday.entity.DayPlan;
import dk.tommer.workday.entity.MaterialOrder;
import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.DayPlanRepository;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.repository.WorkLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard/svend")
public class SvendDashboardController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DayPlanRepository dayPlanRepository;
    @Autowired
    private MaterialOrderRepository materialOrderRepository;
    @Autowired
    private WorkLogRepository workLogRepository;

    @GetMapping
    public SvendDashboardDTO getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Long userId = user.getId();
        LocalDate today = LocalDate.now();

        Optional<DayPlan> todayPlan = dayPlanRepository.findFirstByUser_IdAndDateOrderByDateAsc(userId, today);
        ProjectSummaryDTO currentProject = todayPlan
                .map(DayPlan::getProject)
                .map(p -> new ProjectSummaryDTO(p.getId(), p.getName(), p.getAddress(), p.getDescription()))
                .orElse(null);

        List<DayPlanDTO> calendarPreview = dayPlanRepository
                .findTop5ByUser_IdAndDateGreaterThanEqualOrderByDateAsc(userId, today)
                .stream()
                .map(dp -> {
                    Project p = dp.getProject();
                    return new DayPlanDTO(dp.getDate(), p != null ? p.getId() : null, p != null ? p.getName() : null,
                            p != null ? p.getAddress() : null, p != null ? p.getDescription() : null);
                })
                .collect(Collectors.toList());

        List<MaterialOrderDTO> materialStatus = materialOrderRepository
                .findTop3ByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mo -> new MaterialOrderDTO(mo.getId(), mo.getDescription(), mo.getStatus(), mo.getCreatedAt()))
                .collect(Collectors.toList());

        Double dailyTotalHours = workLogRepository.sumHoursByUserIdAndDate(userId, today);

        SvendDashboardDTO dto = new SvendDashboardDTO();
        dto.setCurrentProject(currentProject);
        dto.setCalendarPreview(calendarPreview);
        dto.setMaterialStatus(materialStatus);
        dto.setDailyTotalHours(dailyTotalHours != null ? dailyTotalHours : 0.0);
        return dto;
    }
}
