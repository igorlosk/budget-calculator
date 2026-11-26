package com.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyBudgetSummary(
        LocalDate date,
        BigDecimal availableAtStartOfDay,
        BigDecimal spentToday,
        BigDecimal balanceThroughDate,
        BigDecimal remainingMonthlyBudget
) {}
