package com.expensetracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

public class UserLevelCalculatorTest {

    private UserLevelCalculator userLevelCalculator;
    private BigDecimal budget;

    @BeforeEach
    void setUp() {
        userLevelCalculator = new UserLevelCalculator();
        budget = new BigDecimal("1000.00");
    }

    @Test
    void promotesWhenSpendingIsBelowBudget() {
        BigDecimal expense = new BigDecimal("999.99");

        assertEquals(UserLevel.PRACTITIONER, userLevelCalculator.nextLevel(UserLevel.AMATEUR, budget, expense));

    }

    @Test
    void promotesWhenSpendingEqualsBudget(){
        BigDecimal expense = new BigDecimal("1000");

        assertEquals(UserLevel.PRACTITIONER, userLevelCalculator.nextLevel(UserLevel.AMATEUR, budget, expense));
    }

    @Test
    void demotesWhenSpendingExceedsBudget(){
        BigDecimal expense = new BigDecimal("1000.01");

        assertEquals(UserLevel.NOVICE, userLevelCalculator.nextLevel(UserLevel.AMATEUR, budget, expense));
    }

    @Test
    void keepsCurrentLevelWhenMonthHasNoExpenses() {
        UserLevel result = userLevelCalculator.nextLevel(
                UserLevel.AMATEUR, budget, BigDecimal.ZERO);

        assertEquals(UserLevel.AMATEUR, result);
    }
}
