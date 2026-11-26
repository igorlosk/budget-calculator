package com.expensetracker;

import java.math.BigDecimal;
import java.util.Objects;

public record MonthlyBudgetRequest(
        BigDecimal amount
) {

    public MonthlyBudgetRequest {
        Objects.requireNonNull(amount, "amount is required");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("amount must be zero or greater");
        }
    }
}
