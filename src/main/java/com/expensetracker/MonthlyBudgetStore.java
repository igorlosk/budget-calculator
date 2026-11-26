package com.expensetracker;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MonthlyBudgetStore {

    private final Map<YearMonth, BigDecimal> budgetsByMonth = new ConcurrentHashMap<>();

    public MonthlyBudgetStore() {
        save(YearMonth.of(2026, Month.NOVEMBER), new BigDecimal("30000"));
    }

    public boolean save(YearMonth month, BigDecimal amount) {
        Objects.requireNonNull(month, "month is required");
        Objects.requireNonNull(amount, "amount is required");
        return budgetsByMonth.put(month, amount) == null;
    }

    public Optional<BigDecimal> find(YearMonth month) {
        Objects.requireNonNull(month, "month is required");
        return Optional.ofNullable(budgetsByMonth.get(month));
    }
}
