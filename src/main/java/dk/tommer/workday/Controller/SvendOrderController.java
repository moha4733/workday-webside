package dk.tommer.workday.Controller;

import dk.tommer.workday.entity.MaterialOrder;
import dk.tommer.workday.entity.Project;
import dk.tommer.workday.entity.User;
import dk.tommer.workday.repository.MaterialOrderRepository;
import dk.tommer.workday.repository.ProjectRepository;
import dk.tommer.workday.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/svend")
public class SvendOrderController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MaterialOrderRepository materialOrderRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @PostMapping("/material-order")
    public String createMaterialOrder(@RequestParam(required = false) Double grossArea,
                                      @RequestParam(required = false) String orderDescription,
                                      @RequestParam(required = false) String type,
                                      @RequestParam(required = false) Long projectId,
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
        if (projectId != null) {
            Project project = projectRepository.findById(projectId).orElse(null);
            if (project != null && project.getAssignedUser() != null && project.getAssignedUser().getEmail().equals(email)) {
                order.setProject(project);
            }
        }
        materialOrderRepository.save(order);
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
}
