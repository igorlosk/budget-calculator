package com.expensetracker;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ExpenseStore {

    private final List<Expense> expenses = new CopyOnWriteArrayList<>(List.of(
            new Expense(LocalDate.of(2026, 11, 2), new BigDecimal("700"), "Еда", true),
            new Expense(LocalDate.of(2026, 11, 3), new BigDecimal("1200"), "Еда", true),
            new Expense(LocalDate.of(2026, 11, 3), new BigDecimal("150"), "Еда", false)
    ));

    public void add(Expense expense) {
        expenses.add(expense);
    }

    public List<Expense> findAll() {
        return List.copyOf(expenses);
    }

}
