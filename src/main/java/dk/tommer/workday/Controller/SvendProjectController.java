package dk.tommer.workday.Controller;

import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.ProjectStatus;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.repository.WorkLogRepository;
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
@RequestMapping("/svend/projects")
public class SvendProjectController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private WorkLogRepository workLogRepository;

    @GetMapping
    public String projects(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Project> myProjects = projectRepository.findByAssignedUser_Id(user.getId());
        model.addAttribute("userName", user.getName());
        model.addAttribute("myProjects", myProjects);
        return "svend-projects";
    }

    @GetMapping("/{id}/photo")
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

    @PostMapping("/{id}/photo")
    public String uploadProjectPhoto(@PathVariable Long id,
                                     @RequestParam("image") org.springframework.web.multipart.MultipartFile image,
                                     RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null || project.getAssignedUser() == null || project.getAssignedUser().getId() != user.getId()) {
            return "redirect:/svend/projects";
        }
        if (image != null && !image.isEmpty()) {
            // BASIC SECURITY VALIDATION
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                redirectAttributes.addFlashAttribute("error", "Kun billedfiler er tilladt!");
                return "redirect:/svend/projects";
            }
            
            try {
                java.nio.file.Path dir = java.nio.file.Paths.get("uploads", String.valueOf(project.getId()));
                java.nio.file.Files.createDirectories(dir);
                String filename = java.time.LocalDateTime.now().toString().replace(":", "-") + "-" + image.getOriginalFilename();
                // Basic sanitization
                filename = filename.replaceAll("[^a-zA-Z0-9.-]", "_");
                
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

    @PostMapping("/{id}/status")
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

    @GetMapping("/{id}/log-hours")
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

    @PostMapping("/{id}/log-hours")
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
}
