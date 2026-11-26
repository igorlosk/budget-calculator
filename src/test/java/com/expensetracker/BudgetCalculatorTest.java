package com.expensetracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        // Из плана на выбранную дату вычитаются только расходы текущего месяца, совершённые до начала этого дня.
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

    @Test
    void dailySummaryAddsAllExpensesOnSelectedDate() {
        // Такой тест поймает две возможные ошибки: если метод учтёт только одну покупку за день или пропустит расход без отметки «обязательный».
        BigDecimal budget = new BigDecimal("30000");
        LocalDate november2 = LocalDate.of(2026, 11, 2);
        LocalDate november3 = LocalDate.of(2026, 11, 3);

        List<Expense> expenses = List.of(
                new Expense(november2, new BigDecimal("700"), "Еда", true),
                new Expense(november3, new BigDecimal("1200"), "Еда", true),
                new Expense(november3, new BigDecimal("150"), "Еда", false)
        );

        DailyBudgetSummary expected = new DailyBudgetSummary(
                november3,
                new BigDecimal("2300.00"),
                new BigDecimal("1350"),
                new BigDecimal("950.00"),
                new BigDecimal("27950")
        );

        assertEquals(expected, calculator.dailySummary(budget, expenses, november3));
    }

    @Test
    void summarizesExpensesForRequestedMonth() {
        List<Expense> expenses = List.of(
                new Expense(LocalDate.of(2026, 10, 31), new BigDecimal("900"), "Книги", true),
                new Expense(LocalDate.of(2026, 11, 1),  new BigDecimal("700"), "Еда", true),
                new Expense(LocalDate.of(2026, 11, 3),  new BigDecimal("1200"), "Еда", true),
                new Expense(LocalDate.of(2026, 11, 3),  new BigDecimal("150"), "Еда", false),
                new Expense(LocalDate.of(2026, 11, 30), new BigDecimal("250"), "Транспорт", true),
                new Expense(LocalDate.of(2026, 12, 1),  new BigDecimal("400"), "Одежда", false)
        );

        MonthlyExpenseReport report =
                calculator.monthlyExpenseReport(expenses, YearMonth.of(2026, 11));

        assertEquals(0, report.totalSpent().compareTo(new BigDecimal("2300")));
        assertEquals(0, report.mandatorySpent().compareTo(new BigDecimal("2150")));
        assertEquals(0, report.unmarkedSpent().compareTo(new BigDecimal("150")));

        assertEquals(Set.of("Еда", "Транспорт"), report.byCategory().keySet());
        assertEquals(0, report.byCategory().get("Еда").compareTo(new BigDecimal("2050")));
        assertEquals(0, report.byCategory().get("Транспорт").compareTo(new BigDecimal("250")));
    }

    @Test
    void returnsZeroTotalsAndEmptyCategoriesWhenMonthHasNoExpenses() {
        List<Expense> expenses = List.of(
                new Expense(LocalDate.of(2026, 11, 30), new BigDecimal("250"), "Транспорт", true)
        );

        MonthlyExpenseReport report =
                calculator.monthlyExpenseReport(expenses, YearMonth.of(2026, 12));

        assertEquals(0, report.totalSpent().signum());
        assertEquals(0, report.mandatorySpent().signum());
        assertEquals(0, report.unmarkedSpent().signum());
        assertTrue(report.byCategory().isEmpty());
    }

}