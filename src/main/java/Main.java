import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        Expense expense1 = new Expense(LocalDate.of(2026, 11, 2), new BigDecimal("700"), "Еда", true);
        Expense expense2 = new Expense(LocalDate.of(2026, 11, 3), new BigDecimal("1200"), "Еда", true);
        Expense expense3 = new Expense(LocalDate.of(2026, 10, 31), new BigDecimal("1200"), "Еда", true);
        Expense expense4 = new Expense(LocalDate.of(2026, 11, 3), new BigDecimal("150"), "Еда", false);
        List<Expense> expenses = new ArrayList<>();
        expenses.add(expense1);
        expenses.add(expense2);
        expenses.add(expense3);
        expenses.add(expense4);

        BudgetCalculator budgetCalculator = new BudgetCalculator();

        System.out.println(budgetCalculator.availableAtStartOfDay(new BigDecimal("30000"), expenses, LocalDate.of(2026, 11, 1)));
        System.out.println(budgetCalculator.availableAtStartOfDay(new BigDecimal("30000"), expenses, LocalDate.of(2026, 11, 2)));
        System.out.println(budgetCalculator.availableAtStartOfDay(new BigDecimal("30000"), expenses, LocalDate.of(2026, 11, 3)));

        System.out.println(budgetCalculator.dailySummary(new BigDecimal("30000"), expenses, LocalDate.of(2026, 11, 3)));

    }
}
