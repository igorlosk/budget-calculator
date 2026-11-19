import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BudgetCalculatorTest {

    private BudgetCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new BudgetCalculator();
    }

    @Test
    void countsMandatoryExpenseStartingOnItsDate() {
        // Проверяет, какие обязательные расходы попадают в отчёт на выбранную дату
        LocalDate november2 = LocalDate.of(2026, 11, 2);
        LocalDate november3 = november2.plusDays(1);

        List<Expense> expenses = List.of(
                new Expense(november2, new BigDecimal("1000"), "Еда", true),
                new Expense(november2, new BigDecimal("300"), "Еда", false),
                new Expense(november3, new BigDecimal("500"), "Еда", true),
                new Expense(LocalDate.of(2025, 11, 1), new BigDecimal("900"), "Еда", true)
        );

        assertEquals(new BigDecimal("1000"), calculator.mandatorySpentThroughDate(expenses, november2));
        assertEquals(new BigDecimal("1500"), calculator.mandatorySpentThroughDate(expenses, november3));
    }

    @Test
    void plannedThroughDateReturnsFullBudgetOnLastDayOfMonth() {
        // Подтверждает, что к 31 января план достигает 10000.00 без потери двух копеек

        BigDecimal budget = new BigDecimal("10000");
        LocalDate lastDayOfJanuary = LocalDate.of(2027, 1, 31);

        assertEquals(new BigDecimal("10000.00"), calculator.plannedThroughDate(budget, lastDayOfJanuary));
    }

    @Test
    void carriesSavingsIntoNextDay() {
        // Проверить перенос экономии на следующий день.
        BigDecimal budget = new BigDecimal("30000");
        LocalDate november1 = LocalDate.of(2026, 11, 1);
        LocalDate november2 = LocalDate.of(2026, 11, 2);

        List<Expense> expenses = List.of(
                new Expense(november1, new BigDecimal("700"), "Еда", true),
                new Expense(november2, new BigDecimal("1200"), "Еда", false)
        );

        BigDecimal spentThroughNov1 = calculator.totalSpentThroughDate(expenses, november1);
        BigDecimal spentThroughNov2 = calculator.totalSpentThroughDate(expenses, november2);

        assertEquals(new BigDecimal("300.00"), calculator.balanceThroughDate(budget, november1, spentThroughNov1));
        assertEquals(new BigDecimal("100.00"), calculator.balanceThroughDate(budget, november2, spentThroughNov2));
    }

    @Test
    void availableAtStartOfDaySubtractsOnlyEarlierExpensesInSameMonth() {
        BigDecimal budget = new BigDecimal("30000");
        LocalDate november1 = LocalDate.of(2026, 11, 1);
        LocalDate november2 = LocalDate.of(2026, 11, 2);
        LocalDate november3 = LocalDate.of(2026, 11, 3);
        List<Expense> expenses = List.of(
                new Expense(november2, new BigDecimal("700"), "Еда", true),
                new Expense(november3, new BigDecimal("1200"), "Еда", true),
                new Expense(LocalDate.of(2026, 10, 31), new BigDecimal("1200"), "Еда", true)
        );

        assertEquals(new BigDecimal("1000.00"), calculator.availableAtStartOfDay(budget, expenses, november1));
        assertEquals(new BigDecimal("2000.00"), calculator.availableAtStartOfDay(budget, expenses, november2));
        assertEquals(new BigDecimal("2300.00"), calculator.availableAtStartOfDay(budget, expenses, november3));
    }
}