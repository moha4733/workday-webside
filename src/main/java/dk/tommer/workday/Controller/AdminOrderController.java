package dk.tommer.workday.controller;

import dk.tommer.workday.entity.MaterialOrder;
import dk.tommer.workday.entity.MaterialStatus;
import dk.tommer.workday.repository.MaterialOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {
    @Autowired
    private MaterialOrderRepository materialOrderRepository;

    @GetMapping
    public String listOrders(Model model) {
        List<MaterialOrder> pending = materialOrderRepository.findByStatusOrderByCreatedAtDesc(MaterialStatus.PENDING);
        List<MaterialOrder> all = materialOrderRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("pendingOrders", pending);
        model.addAttribute("allOrders", all);
        return "admin-orders";
    }

    @PostMapping("/{id}/approve")
    public String approveOrder(@PathVariable Long id) {
        MaterialOrder order = materialOrderRepository.findById(id).orElseThrow();
        order.setStatus(MaterialStatus.APPROVED);
        materialOrderRepository.save(order);
        return "redirect:/admin/orders";
    }
}
