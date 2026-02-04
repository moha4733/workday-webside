package dk.tommer.workday.dto;

import java.math.BigDecimal;

public class AccountingStatsDTO {
    private PeriodStats month;
    private PeriodStats quarter;
    private PeriodStats year;

    public AccountingStatsDTO(PeriodStats month, PeriodStats quarter, PeriodStats year) {
        this.month = month;
        this.quarter = quarter;
        this.year = year;
    }

    public PeriodStats getMonth() { return month; }
    public PeriodStats getQuarter() { return quarter; }
    public PeriodStats getYear() { return year; }

    public static class PeriodStats {
        private BigDecimal earnings;
        private BigDecimal costs;
        private BigDecimal profit;
        private long projectCount;

        public PeriodStats(BigDecimal earnings, BigDecimal costs, BigDecimal profit, long projectCount) {
            this.earnings = earnings;
            this.costs = costs;
            this.profit = profit;
            this.projectCount = projectCount;
        }

        public BigDecimal getEarnings() { return earnings; }
        public BigDecimal getCosts() { return costs; }
        public BigDecimal getProfit() { return profit; }
        public long getProjectCount() { return projectCount; }
    }
}
