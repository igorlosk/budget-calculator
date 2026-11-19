import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        Expense expense1 = new Expense(LocalDate.of(2026, 11, 2), new BigDecimal("700"), "Еда", true);
        Expense expense2 = new Expense(LocalDate.of(2026, 11, 3), new BigDecimal("1200"), "Еда", true);
        Expense expense3 = new Expense(LocalDate.of(2026, 10, 31), new BigDecimal("1200"), "Еда", true);
        List<Expense> expenses = new ArrayList<>();
        expenses.add(expense1);
        expenses.add(expense2);
        expenses.add(expense3);

        BudgetCalculator budgetCalculator = new BudgetCalculator();

        System.out.println(budgetCalculator.availableAtStartOfDay(new BigDecimal("30000"), expenses, LocalDate.of(2026, 11, 1)));
        System.out.println(budgetCalculator.availableAtStartOfDay(new BigDecimal("30000"), expenses, LocalDate.of(2026, 11, 2)));
        System.out.println(budgetCalculator.availableAtStartOfDay(new BigDecimal("30000"), expenses, LocalDate.of(2026, 11, 3)));

    }
}
