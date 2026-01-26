package dk.tommer.workday.controller;

import dk.tommer.workday.entity.User;
import dk.tommer.workday.entity.WorkLog;
import dk.tommer.workday.entity.WorkLogStatus;
import dk.tommer.workday.repository.UserRepository;
import dk.tommer.workday.repository.WorkLogRepository;
import dk.tommer.workday.Service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin/worklogs")
public class AdminWorkLogController {

    @Autowired
    private WorkLogRepository workLogRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PdfService pdfService;

    @GetMapping
    public String listAllWorkLogs(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.findByEmail(email).orElseThrow();
        
        List<WorkLog> allWorkLogs = workLogRepository.findAll();
        allWorkLogs.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        
        model.addAttribute("workLogs", allWorkLogs);
        model.addAttribute("userName", currentUser.getName());
        model.addAttribute("profilePhotoPath", currentUser.getProfilePhotoPath());
        
        return "admin/worklogs";
    }
    
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadWorkLogsPdf(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws Exception {
        
        // Default to last 30 days if no dates provided
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : end.minusDays(30);
        
        byte[] pdfBytes = pdfService.generateApprovedWorkLogsPdf(start, end, userId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        
        String filename = "godkendte-timer-" + start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                         "-til-" + end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        headers.setContentDispositionFormData("attachment", filename);
        
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
    
    @PostMapping("/{id}/approve")
    public String approveWorkLog(@PathVariable Long id) {
        WorkLog workLog = workLogRepository.findById(id).orElseThrow();
        workLog.setStatus(WorkLogStatus.APPROVED);
        workLog.setAdminComment(null);
        workLogRepository.save(workLog);
        
        return "redirect:/admin/worklogs";
    }
    
    @PostMapping("/{id}/reject")
    public String rejectWorkLog(@PathVariable Long id, @RequestParam(required = false) String comment) {
        WorkLog workLog = workLogRepository.findById(id).orElseThrow();
        workLog.setStatus(WorkLogStatus.REJECTED);
        workLog.setAdminComment(comment);
        workLogRepository.save(workLog);
        
        return "redirect:/admin/worklogs";
    }
    
    @PostMapping("/{id}/pending")
    public String setPendingWorkLog(@PathVariable Long id) {
        WorkLog workLog = workLogRepository.findById(id).orElseThrow();
        workLog.setStatus(WorkLogStatus.PENDING);
        workLog.setAdminComment(null);
        workLogRepository.save(workLog);
        
        return "redirect:/admin/worklogs";
    }
}
