package dk.tommer.workday.Service;

import dk.tommer.workday.dto.BudgetDTO;
import dk.tommer.workday.entity.*;
import dk.tommer.workday.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import dk.tommer.workday.dto.AccountingStatsDTO;

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
                project.getId(),
                project.getName(),
                project.getStatus(),
                budget,
                materialCost,
                laborCost,
                totalSpent,
                remainingBalance,
                spentPercentage
        );
    }

    public List<BudgetDTO> getAllProjectBudgets() {
        return projectRepository.findAll().stream()
                .map(p -> getProjectBudget(p.getId()))
                .collect(Collectors.toList());
    }

    public AccountingStatsDTO getAccountingStats() {
        LocalDate now = LocalDate.now();
        
        // Month stats
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth());
        AccountingStatsDTO.PeriodStats monthStats = calculatePeriodStats(startOfMonth, endOfMonth);
        
        // Quarter stats
        int currentMonth = now.getMonthValue();
        int startMonthOfQuarter = ((currentMonth - 1) / 3) * 3 + 1;
        LocalDate startOfQuarter = LocalDate.of(now.getYear(), startMonthOfQuarter, 1);
        LocalDate endOfQuarter = startOfQuarter.plusMonths(3).minusDays(1);
        AccountingStatsDTO.PeriodStats quarterStats = calculatePeriodStats(startOfQuarter, endOfQuarter);
        
        // Year stats
        LocalDate startOfYear = now.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = now.with(TemporalAdjusters.lastDayOfYear());
        AccountingStatsDTO.PeriodStats yearStats = calculatePeriodStats(startOfYear, endOfYear);
        
        return new AccountingStatsDTO(monthStats, quarterStats, yearStats);
    }

    private AccountingStatsDTO.PeriodStats calculatePeriodStats(LocalDate startDir, LocalDate endDir) {
        LocalDateTime start = startDir.atStartOfDay();
        LocalDateTime end = endDir.atTime(LocalTime.MAX);
        
        // Use standard rate
        Double hourlyRate = companyRepository.findFirstByOrderByIdAsc()
                .map(Company::getStandardHourlyRate)
                .filter(rate -> rate != null)
                .orElse(0.0);

        // 1. Costs from Material Orders
        List<MaterialOrder> orders = materialOrderRepository.findByStatusAndCreatedAtBetween(MaterialStatus.APPROVED, start, end);
        BigDecimal materialCost = orders.stream()
                .map(o -> o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Costs from Work Logs
        List<WorkLog> workLogs = workLogRepository.findByStatusAndDateBetweenOrderByDateAsc(WorkLogStatus.APPROVED, startDir, endDir);
        Double totalHours = workLogs.stream()
                .mapToDouble(WorkLog::getHours)
                .sum();
        BigDecimal laborCost = BigDecimal.valueOf(totalHours * hourlyRate).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalCosts = materialCost.add(laborCost);
        
        // For "Earnings", we'll sum the budgets of projects that STARTED in this period
        List<Project> projectsStarted = projectRepository.findAll().stream()
                .filter(p -> p.getStartDate() != null && !p.getStartDate().isBefore(startDir) && !p.getStartDate().isAfter(endDir))
                .collect(Collectors.toList());
        
        BigDecimal totalEarnings = projectsStarted.stream()
                .map(p -> p.getBudget() != null ? p.getBudget() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal profit = totalEarnings.subtract(totalCosts);
        
        return new AccountingStatsDTO.PeriodStats(totalEarnings, totalCosts, profit, projectsStarted.size());
    }
}
