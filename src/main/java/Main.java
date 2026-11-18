import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class Main {
    public static void main(String[] args) {
        BudgetCalculator budgetCalculator = new BudgetCalculator();

        YearMonth november = YearMonth.of(2026, 11);
        BigDecimal bigDecimaNov = budgetCalculator.dailyLimit(new BigDecimal("30000"), november);
        System.out.println(bigDecimaNov);

        LocalDate localDate1 = LocalDate.of(2026, 11, 2);
        BigDecimal bigDecimal1 = budgetCalculator.plannedThroughDate(new BigDecimal("30000"), localDate1);
        System.out.println(bigDecimal1);

        System.out.println(
                budgetCalculator.balanceThroughDate(new BigDecimal("30000"),
                        localDate1,
                        new BigDecimal("2300")));

        YearMonth february = YearMonth.of(2028, 2);
        BigDecimal bigDecimaFeb = budgetCalculator.dailyLimit(new BigDecimal("29000"), february);
        System.out.println(bigDecimaFeb);

        LocalDate localDate2 = LocalDate.of(2027, 1, 31);
        BigDecimal bigDecimal2 = budgetCalculator.plannedThroughDate(new BigDecimal("10000"), localDate2);
        System.out.println(bigDecimal2);

        YearMonth january = YearMonth.of(2027, 1);
        BigDecimal jan = budgetCalculator.dailyLimit(new BigDecimal("10000"), january);
        System.out.println(jan);
    }
}
