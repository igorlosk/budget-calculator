import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

public class BudgetCalculator {

    public BigDecimal dailyLimit(BigDecimal monthlyBudget, YearMonth month) {
        int daysInMonth = month.lengthOfMonth();
        return monthlyBudget.divide(
                BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal plannedThroughDate(BigDecimal monthlyBudget, LocalDate date) {
        int dayOfMonth = date.getDayOfMonth();
        int daysInMonth = date.lengthOfMonth();

        return monthlyBudget.multiply(BigDecimal.valueOf(dayOfMonth))
                .divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal balanceThroughDate(
            BigDecimal monthlyBudget,
            LocalDate date,
            BigDecimal spentThroughDate) {
        BigDecimal plannedAmount = plannedThroughDate(monthlyBudget, date);
        return plannedAmount.subtract(spentThroughDate);
    }

}
