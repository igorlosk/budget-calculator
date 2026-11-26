package com.expensetracker;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
public class BudgetController {

    private final BudgetCalculator budgetCalculator;
    private final ExpenseStore expenseStore;
    private final MonthlyBudgetStore monthlyBudgetStore;


    public BudgetController(
            BudgetCalculator budgetCalculator,
            ExpenseStore expenseStore,
            MonthlyBudgetStore monthlyBudgetStore) {
        this.budgetCalculator = budgetCalculator;
        this.expenseStore = expenseStore;
        this.monthlyBudgetStore = monthlyBudgetStore;
    }

    @GetMapping("/api/budget/daily-summary")
    public DailyBudgetSummary dailySummary(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        BigDecimal budget = monthlyBudgetStore.find(YearMonth.from(date))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Бюджет на этот месяц ещё не задан"));
        return budgetCalculator.dailySummary(budget, expenseStore.findAll(), date);

    }

    @PostMapping("/api/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public void addExpense(@RequestBody Expense expense) {
        expenseStore.add(expense);
    }

    @PutMapping("/api/budgets/{month}")
    public ResponseEntity<Void> saveMonthlyBudget(
            @PathVariable("month") @DateTimeFormat(pattern = "uuuu-MM") YearMonth month,
            @RequestBody MonthlyBudgetRequest request) {

        boolean created = monthlyBudgetStore.save(month, request.amount());

        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/budget/category-report")
    public Map<String, BigDecimal> ByCategoryThroughDate(
            @RequestParam("month") @DateTimeFormat(pattern = "uuuu-MM") YearMonth month) {
        List<Expense> all = expenseStore.findAll();
        return budgetCalculator.totalByCategoryThroughDate(all, month.atEndOfMonth());
    }

    @GetMapping("/api/budget/monthly-report")
    public MonthlyExpenseReport monthlyExpenseReport(
            @RequestParam("month") @DateTimeFormat(pattern = "uuuu-MM") YearMonth month) {
        return budgetCalculator.monthlyExpenseReport(expenseStore.findAll(), month);
    }
}
