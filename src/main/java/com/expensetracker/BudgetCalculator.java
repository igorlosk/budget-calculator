package com.expensetracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetCalculator {

    public BigDecimal dailyLimit(
            BigDecimal monthlyBudget,
            YearMonth month) {
        // Возвращает лимит на день
        int daysInMonth = month.lengthOfMonth();
        return monthlyBudget.divide(
                BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal plannedThroughDate(
            BigDecimal monthlyBudget,
            LocalDate date) {
        // Возвращает сумму, которую по плану можно потратить с начала месяца по указанную дату включительно
        int dayOfMonth = date.getDayOfMonth();
        int daysInMonth = date.lengthOfMonth();

        return monthlyBudget.multiply(BigDecimal.valueOf(dayOfMonth))
                .divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal balanceThroughDate(
            BigDecimal monthlyBudget,
            LocalDate date,
            BigDecimal spentThroughDate) {
        // Принимает общую сумму расходов с начала месяца по указанную дату включительно
        BigDecimal plannedAmount = plannedThroughDate(monthlyBudget, date);
        return plannedAmount.subtract(spentThroughDate);
    }

    public BigDecimal totalSpentThroughDate(
            List<Expense> expenses,
            LocalDate date) {
        // Суммирует расходы с первого числа месяца указанной даты по эту дату включительно
        BigDecimal totalSpent = BigDecimal.ZERO;
        LocalDate startOfMonth = date.withDayOfMonth(1);

        for (Expense expense : expenses) {
            LocalDate expenseDate = expense.date();

            if (!expenseDate.isBefore(startOfMonth)
                    && !expenseDate.isAfter(date)) {
                totalSpent = totalSpent.add(expense.amount());
            }
        }
        return totalSpent;
    }

    public BigDecimal remainingBudgetThroughDate(
            BigDecimal monthlyBudget,
            List<Expense> expenses,
            LocalDate date) {
        // Получать остаток месячного бюджета из списка расходов
        return monthlyBudget.subtract(totalSpentThroughDate(expenses, date));
    }

    public Map<String, BigDecimal> totalByCategoryThroughDate(
            List<Expense> expenses,
            LocalDate date) {
        // Должен возвращать суммы по каждой категории с начала месяца по указанную дату включительно
        LocalDate startOfMonth = date.withDayOfMonth(1);

        Map<String, BigDecimal> totalsByCategory = new HashMap<>();

        for (Expense expense : expenses) {
            LocalDate expenseDate = expense.date();

            if (!expenseDate.isBefore(startOfMonth)
                    && !expenseDate.isAfter(date)) {
                String category = expense.category();
                BigDecimal previousTotal = totalsByCategory.getOrDefault(category, BigDecimal.ZERO);
                totalsByCategory.put(category, previousTotal.add(expense.amount()));

            }
        }
        return totalsByCategory;
    }

    public BigDecimal mandatorySpentThroughDate(List<Expense> expenses, LocalDate date) {
        List<Expense> mandatoryExpenses = expenses.stream().filter(Expense::mandatory).toList();
        return totalSpentThroughDate(mandatoryExpenses, date);
    }

    public BigDecimal availableAtStartOfDay(
            BigDecimal monthlyBudget,
            List<Expense> expenses,
            LocalDate date) {
        // Показать доступную сумму на начало дня, метод использует вчерашнюю дату только со второго числа месяца

        BigDecimal plannedAmount = plannedThroughDate(monthlyBudget, date);

        if (date.getDayOfMonth() == 1) {
            return plannedAmount;
        }

        BigDecimal spentBeforeToday = totalSpentThroughDate(expenses, date.minusDays(1));
        return plannedAmount.subtract(spentBeforeToday);
    }

    public DailyBudgetSummary dailySummary(
            BigDecimal monthlyBudget,
            List<Expense> expenses,
            LocalDate date) {

        BigDecimal spentToday = BigDecimal.ZERO;
        for (Expense expense : expenses) {
            if (expense.date().equals(date)) {
                spentToday = spentToday.add(expense.amount());
            }
        }

        BigDecimal spentThroughDate = totalSpentThroughDate(expenses, date);
        BigDecimal availableAtStart = availableAtStartOfDay(monthlyBudget, expenses, date);
        BigDecimal balance = balanceThroughDate(monthlyBudget, date, spentThroughDate);
        BigDecimal remainingBudget = monthlyBudget.subtract(spentThroughDate);

        return new DailyBudgetSummary(
                date,
                availableAtStart,
                spentToday,
                balance,
                remainingBudget
        );
    }

    public MonthlyExpenseReport monthlyExpenseReport(List<Expense> expenses, YearMonth month){
        LocalDate endOfMonth = month.atEndOfMonth();
        BigDecimal totalSpent = totalSpentThroughDate(expenses, endOfMonth);
        BigDecimal mandatorySpent = mandatorySpentThroughDate(expenses, endOfMonth);
        Map<String, BigDecimal> totalByCategory = totalByCategoryThroughDate(expenses, endOfMonth);
        BigDecimal unmarkedSpent = totalSpent.subtract(mandatorySpent);
        return new MonthlyExpenseReport(
                totalSpent,
                mandatorySpent,
                unmarkedSpent,
                totalByCategory
        );
    }


}
