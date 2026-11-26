package com.expensetracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
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
        BigDecimal budget = monthlyBudget.get().setScale(2, RoundingMode.HALF_EVEN);
        List<Expense> expenseList = expenseStore.findAll();
        MonthlyExpenseReport monthlyExpenseReport = budgetCalculator.monthlyExpenseReport(expenseList, month);
        return userLevelCalculator.nextLevel(currentLevel, budget, monthlyExpenseReport.totalSpent());
    }

}
