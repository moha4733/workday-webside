package dk.tommer.workday.controller;

import dk.tommer.workday.dto.DayPlanDTO;
import dk.tommer.workday.dto.MaterialOrderDTO;
import dk.tommer.workday.dto.ProjectSummaryDTO;
import dk.tommer.workday.entity.DayPlan;
import dk.tommer.workday.entity.MaterialOrder;
import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.ProjectStatus;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.DayPlanRepository;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.repository.WorkLogRepository;
import dk.tommer.workday.service.MaterialCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/svend")
public class SvendPageController {
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
            List<Project> assigned = projectRepository.findByAssignedUser_Id(userId);
            Project p = assigned.stream().filter(pr -> pr.getStatus() == ProjectStatus.IN_PROGRESS).findFirst().orElse(assigned.isEmpty() ? null : assigned.get(0));
            if (p != null) {
                for (int i = 0; i < 5; i++) {
                    calendarPreview.add(new DayPlanDTO(today.plusDays(i), p.getId(), p.getName(), p.getAddress(), p.getDescription(), p.getStartTime()));
                }
            }
        }

        List<MaterialOrderDTO> materialStatus = materialOrderRepository
                .findTop3ByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mo -> new MaterialOrderDTO(mo.getId(), mo.getDescription(), mo.getStatus(), mo.getCreatedAt()))
                .collect(Collectors.toList());
        String latestOrderStatus = materialStatus.isEmpty() ? "Ingen" : materialStatus.get(0).getStatus().name();

        Double dailyTotalHours = workLogRepository.sumHoursByUserIdAndDate(userId, today);

        model.addAttribute("userName", user.getName());
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
        return "svend-dashboard";
    }

    @PostMapping("/material-order")
    public String createMaterialOrder(@RequestParam(required = false) Double grossArea,
                                      @RequestParam(required = false) String orderDescription,
                                      @RequestParam(required = false) String type,
                                      @RequestParam(required = false) String floorType,
                                      @RequestParam(required = false) String insulationType,
                                      @RequestParam(required = false) String gypsumType,
                                      @RequestParam(required = false) String battensType,
                                      @RequestParam(required = false) String windowTrimType,
                                      @RequestParam(required = false) String addressNote) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        MaterialOrder order = new MaterialOrder();
        order.setUser(userRepository.findByEmail(email).orElseThrow());
        if (orderDescription != null && !orderDescription.isBlank()) {
            order.setDescription(orderDescription);
        } else if (grossArea != null) {
            order.setDescription("Gulvmateriale bestilling: " + grossArea + " m2");
        } else {
            order.setDescription("Materialeanmodning fra SVEND");
        }
        String base = order.getDescription() != null ? order.getDescription() : "";
        String t = type != null ? type.toLowerCase() : null;
        if ("floor".equals(t)) {
            if (floorType != null && !floorType.isBlank()) {
                base = base + " | Gulvtype: " + floorType.trim();
            }
        } else if ("insulation".equals(t)) {
            if (insulationType != null && !insulationType.isBlank()) {
                base = base + " | Isoleringstype: " + insulationType.trim();
            }
            if (gypsumType != null && !gypsumType.isBlank()) {
                base = base + " | Gips: " + gypsumType.trim();
            }
        } else if ("battens".equals(t)) {
            if (battensType != null && !battensType.isBlank()) {
                base = base + " | Lægter: " + battensType.trim();
            }
        } else if ("windows".equals(t)) {
            if (windowTrimType != null && !windowTrimType.isBlank()) {
                base = base + " | Vinduer/Lister: " + windowTrimType.trim();
            }
        }
        if (addressNote != null && !addressNote.isBlank()) {
            base = base + " | Adresse: " + addressNote.trim();
        }
        order.setDescription(base);
        materialOrderRepository.save(order);
        return "redirect:/svend/dashboard";
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Project> myProjects = projectRepository.findByAssignedUser_Id(user.getId());
        model.addAttribute("userName", user.getName());
        model.addAttribute("myProjects", myProjects);
        return "svend-projects";
    }

    @GetMapping("/projects/{id}/photo")
    public String showProjectPhoto(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null || project.getAssignedUser() == null || project.getAssignedUser().getId() != user.getId()) {
            return "redirect:/svend/projects";
        }
        model.addAttribute("userName", user.getName());
        model.addAttribute("project", project);
        return "svend-project-photo";
    }

    @PostMapping("/projects/{id}/photo")
    public String uploadProjectPhoto(@PathVariable Long id,
                                     @org.springframework.web.bind.annotation.RequestParam("image") org.springframework.web.multipart.MultipartFile image,
                                     RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null || project.getAssignedUser() == null || project.getAssignedUser().getId() != user.getId()) {
            return "redirect:/svend/projects";
        }
        if (image != null && !image.isEmpty()) {
            try {
                java.nio.file.Path dir = java.nio.file.Paths.get("uploads", String.valueOf(project.getId()));
                java.nio.file.Files.createDirectories(dir);
                String filename = java.time.LocalDateTime.now().toString().replace(":", "-") + "-" + image.getOriginalFilename();
                java.nio.file.Path target = dir.resolve(filename);
                image.transferTo(target.toFile());
                redirectAttributes.addFlashAttribute("success", "Billede uploadet!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Upload fejlede: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Ingen fil valgt");
        }
        return "redirect:/svend/projects";
    }
    @PostMapping("/projects/{id}/status")
    public String updateProjectStatus(@PathVariable Long id,
                                      @RequestParam String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Project project = projectRepository.findById(id).orElse(null);
        if (project != null && project.getAssignedUser() != null && project.getAssignedUser().getId() == user.getId()) {
            ProjectStatus newStatus = null;
            String s = status != null ? status.toUpperCase() : "";
            if ("IN_PROGRESS".equals(s) || "STARTET".equalsIgnoreCase(status) || "IGANG".equalsIgnoreCase(status)) {
                newStatus = ProjectStatus.IN_PROGRESS;
            } else if ("FINISHED".equals(s) || "FAERDIG".equalsIgnoreCase(status) || "FÆRDIG".equalsIgnoreCase(status)) {
                newStatus = ProjectStatus.FINISHED;
            }
            if (newStatus != null) {
                project.setStatus(newStatus);
                projectRepository.save(project);
            }
        }
        return "redirect:/svend/projects";
    }
    @GetMapping("/projects/{id}/log-hours")
    public String showLogHours(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null || project.getAssignedUser() == null || project.getAssignedUser().getId() != user.getId()) {
            redirectAttributes.addFlashAttribute("error", "Du kan kun registrere timer på dine egne projekter.");
            return "redirect:/svend/projects";
        }
        model.addAttribute("userName", user.getName());
        model.addAttribute("project", project);
        model.addAttribute("today", LocalDate.now());
        return "svend-log-hours";
    }

    @PostMapping("/projects/{id}/log-hours")
    public String submitLogHours(@PathVariable Long id,
                                 @RequestParam Double hours,
                                 @RequestParam(required = false) LocalDate date,
                                 RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null || project.getAssignedUser() == null || project.getAssignedUser().getId() != user.getId()) {
            redirectAttributes.addFlashAttribute("error", "Du kan kun registrere timer på dine egne projekter.");
            return "redirect:/svend/projects";
        }
        if (hours == null || hours <= 0) {
            redirectAttributes.addFlashAttribute("error", "Timer skal være et positivt tal.");
            return "redirect:/svend/projects/" + id + "/log-hours";
        }
        dk.tommer.workday.entity.WorkLog wl = new dk.tommer.workday.entity.WorkLog();
        wl.setUser(user);
        wl.setProject(project);
        wl.setDate(date != null ? date : LocalDate.now());
        wl.setHours(hours);
        workLogRepository.save(wl);
        redirectAttributes.addFlashAttribute("success", "Timer registreret: " + hours + " t (" + (date != null ? date : LocalDate.now()) + ")");
        return "redirect:/svend/dashboard";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        var orders = materialOrderRepository.findTop3ByUser_IdOrderByCreatedAtDesc(user.getId());
        model.addAttribute("userName", user.getName());
        model.addAttribute("orders", orders);
        return "svend-orders";
    }

    @GetMapping("/calculator")
    public String calculator(Model model,
                             @RequestParam(required = false) String type,
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

    @GetMapping("/settings")
    public String settings(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("userName", user.getName());
        return "svend-settings";
    }
}
