package com.expensetracker;

import java.math.BigDecimal;

public class UserLevelCalculator {

    public UserLevel nextLevel(UserLevel currentLevel, BigDecimal budget, BigDecimal totalSpent) {
        if (totalSpent.signum() == 0){
            return currentLevel;
        }
        if (budget.compareTo(totalSpent) >= 0) {
            return currentLevel.promote();
        }
        return currentLevel.demote();
    }
}
