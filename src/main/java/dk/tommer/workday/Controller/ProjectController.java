package dk.tommer.workday.Controller;

import dk.tommer.workday.Entity.Project;
import dk.tommer.workday.Entity.ProjectStatus;
import dk.tommer.workday.Entity.User;
import dk.tommer.workday.Entity.WorkType;
import dk.tommer.workday.Repo.UserRepository;
import dk.tommer.workday.Repo.WorkTypeRepository;
import dk.tommer.workday.Service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkTypeRepository workTypeRepository;

    @GetMapping
    public String listProjects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("statuses", ProjectStatus.values());
        List<User> users = userRepository.findAll();
        List<WorkType> workTypes = workTypeRepository.findAllByOrderByNameAsc();
        model.addAttribute("users", users);
        model.addAttribute("workTypes", workTypes);
        return "create-project";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute Project project, 
                               @RequestParam(required = false) Long assignedUserId,
                               @RequestParam(required = false) Long workTypeId,
                               RedirectAttributes redirectAttributes) {
        if (assignedUserId != null) {
            User user = userRepository.findById(assignedUserId)
                    .orElse(null);
            project.setAssignedUser(user);
        }
        if (workTypeId != null) {
            WorkType workType = workTypeRepository.findById(workTypeId)
                    .orElse(null);
            project.setWorkType(workType);
        }
        projectService.saveProject(project);
        redirectAttributes.addFlashAttribute("success", "Projekt oprettet succesfuldt!");
        return "redirect:/admin/projects";
    }

    @GetMapping("/{id}/assign")
    public String showAssignForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        List<User> users = userRepository.findAll();
        model.addAttribute("project", project);
        model.addAttribute("users", users);
        return "assign-project";
    }

    @PostMapping("/{id}/assign")
    public String assignProject(@PathVariable Long id, 
                               @RequestParam Long userId,
                               RedirectAttributes redirectAttributes) {
        projectService.assignProjectToUser(id, userId);
        redirectAttributes.addFlashAttribute("success", "Projekt tildelt succesfuldt!");
        return "redirect:/admin/projects";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        model.addAttribute("project", project);
        model.addAttribute("statuses", ProjectStatus.values());
        List<User> users = userRepository.findAll();
        List<WorkType> workTypes = workTypeRepository.findAllByOrderByNameAsc();
        model.addAttribute("users", users);
        model.addAttribute("workTypes", workTypes);
        return "edit-project";
    }

    @PostMapping("/{id}/edit")
    public String updateProject(@PathVariable Long id,
                               @ModelAttribute Project project,
                               @RequestParam(required = false) Long assignedUserId,
                               @RequestParam(required = false) Long workTypeId,
                               RedirectAttributes redirectAttributes) {
        Project existingProject = projectService.getProjectById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        existingProject.setName(project.getName());
        existingProject.setDescription(project.getDescription());
        existingProject.setAddress(project.getAddress());
        existingProject.setStartDate(project.getStartDate());
        existingProject.setStatus(project.getStatus());
        
        if (assignedUserId != null) {
            User user = userRepository.findById(assignedUserId)
                    .orElse(null);
            existingProject.setAssignedUser(user);
        } else {
            existingProject.setAssignedUser(null);
        }
        
        if (workTypeId != null) {
            WorkType workType = workTypeRepository.findById(workTypeId)
                    .orElse(null);
            existingProject.setWorkType(workType);
        } else {
            existingProject.setWorkType(null);
        }
        
        projectService.saveProject(existingProject);
        redirectAttributes.addFlashAttribute("success", "Projekt opdateret succesfuldt!");
        return "redirect:/admin/projects";
    }

    @PostMapping("/{id}/delete")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        projectService.deleteProject(id);
        redirectAttributes.addFlashAttribute("success", "Projekt slettet succesfuldt!");
        return "redirect:/admin/projects";
    }
}

