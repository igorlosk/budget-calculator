import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Expense expense1 = new Expense(LocalDate.of(2026, 11, 1), new BigDecimal("1000"), "Еда", true);
        Expense expense2 = new Expense(LocalDate.of(2026, 11, 2), new BigDecimal("300"), "Еда", false);
        Expense expense3 = new Expense(LocalDate.of(2026, 11, 2), new BigDecimal("1000"), "Кино", false);
        Expense expense4 = new Expense(LocalDate.of(2026, 11, 3), new BigDecimal("500"), "Еда", false);
        Expense expense5 = new Expense(LocalDate.of(2025, 11, 1), new BigDecimal("900"), "Кино", false);
        List<Expense> expenses = new ArrayList<>();
        expenses.add(expense1);
        expenses.add(expense2);
        expenses.add(expense3);
        expenses.add(expense4);
        expenses.add(expense5);


        BudgetCalculator budgetCalculator = new BudgetCalculator();
        LocalDate date = LocalDate.of(2026, 11, 2);
        List<Expense> emptyList = new ArrayList<>();

        Map<String, BigDecimal> stringBigDecimalMap = budgetCalculator.totalByCategoryThroughDate(expenses, date);
        stringBigDecimalMap.forEach((key, value) -> System.out.println(key + ": " + value));

        Map<String, BigDecimal> stringBigDecimalMapEmpty = budgetCalculator.totalByCategoryThroughDate(emptyList, date);
        System.out.println(stringBigDecimalMapEmpty);
    }
}
