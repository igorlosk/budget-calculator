package com.expensetracker;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

public class MonthlyLevelService {

    private final MonthlyBudgetStore monthlyBudgetStore;
    private final ExpenseStore expenseStore;
    private final BudgetCalculator budgetCalculator;
    private final UserLevelCalculator userLevelCalculator;

    public MonthlyLevelService(
            MonthlyBudgetStore monthlyBudgetStore,
            ExpenseStore expenseStore,
            BudgetCalculator budgetCalculator,
            UserLevelCalculator userLevelCalculator) {
        this.monthlyBudgetStore = monthlyBudgetStore;
        this.expenseStore = expenseStore;
        this.budgetCalculator = budgetCalculator;
        this.userLevelCalculator = userLevelCalculator;
    }

    public UserLevel levelAfterMonth(UserLevel currentLevel, YearMonth month) {
        Optional<BigDecimal> monthlyBudget = monthlyBudgetStore.find(month);
        if (monthlyBudget.isEmpty()) {
            return currentLevel;
        }

        MonthlyExpenseReport report =
                budgetCalculator.monthlyExpenseReport(expenseStore.findAll(), month);

        return userLevelCalculator.nextLevel(
                currentLevel, monthlyBudget.get(), report.totalSpent());
    }
}
