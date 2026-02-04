package dk.tommer.workday.controller.admin;

import dk.tommer.workday.Service.ProjectFinanceService;
import dk.tommer.workday.dto.AccountingStatsDTO;
import dk.tommer.workday.dto.BudgetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/accounting")
@PreAuthorize("hasRole('ADMIN')")
public class AccountingController {

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @GetMapping
    public String showAccountingOverview(Model model) {
        AccountingStatsDTO stats = projectFinanceService.getAccountingStats();
        List<BudgetDTO> projectBudgets = projectFinanceService.getAllProjectBudgets();

        model.addAttribute("stats", stats);
        model.addAttribute("projects", projectBudgets);
        
        return "admin/accounting";
    }
}
