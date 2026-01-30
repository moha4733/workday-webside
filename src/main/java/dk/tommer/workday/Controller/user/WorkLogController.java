package dk.tommer.workday.controller.user;

import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.WorkLog;
import dk.tommer.workday.entity.WorkLogStatus;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.repository.WorkLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/svend/worklogs")
public class WorkLogController {

    @Autowired
    private WorkLogRepository workLogRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listWorkLogs(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        List<WorkLog> workLogs = workLogRepository.findByUserOrderByDateDesc(user);
        List<Project> projects = projectRepository.findByAssignedUser_Id(user.getId());
        
        model.addAttribute("workLogs", workLogs);
        model.addAttribute("projects", projects);
        model.addAttribute("userName", user.getName());
        model.addAttribute("profilePhotoPath", user.getProfilePhotoPath());
        
        return "user/worklogs";
    }
    
    @PostMapping
    public String addWorkLog(@RequestParam Long projectId,
                           @RequestParam LocalDate date,
                           @RequestParam Double hours,
                           @RequestParam(required = false) Integer breakMinutes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        Project project = projectRepository.findById(projectId).orElseThrow();
        
        WorkLog workLog = new WorkLog();
        workLog.setUser(user);
        workLog.setProject(project);
        workLog.setDate(date);
        workLog.setHours(hours);
        if (breakMinutes != null && breakMinutes > 0) {
            workLog.setBreakMinutes(breakMinutes);
        }
        workLog.setStatus(WorkLogStatus.PENDING); // New logs are always pending
        
        workLogRepository.save(workLog);
        
        return "redirect:/svend/worklogs";
    }
}
