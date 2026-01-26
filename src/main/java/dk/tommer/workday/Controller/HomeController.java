package dk.tommer.workday.controller;

import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.ProjectStatus;
import dk.tommer.workday.entity.Role;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProjectRepository projectRepository;


    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/")
    public String home(){
        return "welcome";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model){
        // Get current user for profile
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow();
        
        // Hent rigtig data
        List<Project> allProjects = projectRepository.findAll();
        List<Project> activeProjects = allProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS)
                .collect(Collectors.toList());
        
        // Seneste 5 projekter
        List<Project> recentProjects = projectRepository.findTop5ByOrderByIdDesc();
        
        // Beregn total timer (simuleret - kan udvides senere)
        double totalHoursToday = allProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS)
                .mapToDouble(p -> 8.0) // Simuleret: 8 timer per aktivt projekt
                .sum();
        
        model.addAttribute("userName", currentUser.getName());
        model.addAttribute("profilePhotoPath", currentUser.getProfilePhotoPath());
        model.addAttribute("activeProjectsCount", activeProjects.size());
        model.addAttribute("totalProjectsCount", allProjects.size());
        model.addAttribute("totalHoursToday", (int)totalHoursToday);
        model.addAttribute("recentProjects", recentProjects);
        
        return "admin-dashboard";
    }
}
