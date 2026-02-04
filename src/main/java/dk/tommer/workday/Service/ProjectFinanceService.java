package dk.tommer.workday.Service;

import dk.tommer.workday.dto.BudgetDTO;
import dk.tommer.workday.entity.*;
import dk.tommer.workday.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ProjectFinanceService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MaterialOrderRepository materialOrderRepository;

    @Autowired
    private WorkLogRepository workLogRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public BudgetDTO getProjectBudget(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // 1. Total Material Cost
        List<MaterialOrder> orders = materialOrderRepository.findByProjectId(projectId);
        BigDecimal materialCost = orders.stream()
                .filter(o -> o.getStatus() == MaterialStatus.APPROVED)
                .map(o -> o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Total Labor Cost
        List<WorkLog> workLogs = workLogRepository.findByProjectId(projectId);
        Double totalHours = workLogs.stream()
                .filter(w -> w.getStatus() == WorkLogStatus.APPROVED)
                .mapToDouble(WorkLog::getHours)
                .sum();

        // Get company hourly rate (assuming there's only one company or taking the first)
        Double hourlyRate = companyRepository.findFirstByOrderByIdAsc()
                .map(Company::getStandardHourlyRate)
                .filter(rate -> rate != null)
                .orElse(0.0);
        
        if (hourlyRate == 0.0) {
            // Optional: log warning if no rate found
        }

        BigDecimal laborCost = BigDecimal.valueOf(totalHours * hourlyRate).setScale(2, RoundingMode.HALF_UP);

        // 3. Totals
        BigDecimal totalSpent = materialCost.add(laborCost);
        BigDecimal budget = project.getBudget() != null ? project.getBudget() : BigDecimal.ZERO;
        BigDecimal remainingBalance = budget.subtract(totalSpent);

        double spentPercentage = 0;
        if (budget.compareTo(BigDecimal.ZERO) > 0) {
            spentPercentage = totalSpent.divide(budget, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
        } else if (totalSpent.compareTo(BigDecimal.ZERO) > 0) {
            spentPercentage = 100.0; // Budget is 0 but spent > 0
        }

        return new BudgetDTO(
                project.getName(),
                budget,
                materialCost,
                laborCost,
                totalSpent,
                remainingBalance,
                spentPercentage
        );
    }
}
