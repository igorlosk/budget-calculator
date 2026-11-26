package com.expensetracker;

import java.math.BigDecimal;
import java.util.Map;

public record MonthlyExpenseReport(
        BigDecimal totalSpent,
        BigDecimal mandatorySpent,
        BigDecimal unmarkedSpent,
        Map<String, BigDecimal> byCategory
) {
}
