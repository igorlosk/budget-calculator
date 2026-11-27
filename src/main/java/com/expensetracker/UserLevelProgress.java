package com.expensetracker;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class UserLevelProgress {

    private final MonthlyLevelService service;
    private final Map<YearMonth, UserLevel> closedMonths = new HashMap<>();
    private UserLevel currentLevel;

    public UserLevelProgress(UserLevel initialLevel, MonthlyLevelService service) {
        this.currentLevel = initialLevel;
        this.service = service;
    }

    public UserLevel closeMonth(YearMonth month) {

        if (closedMonths.containsKey(month)) {
            return closedMonths.get(month);
        }
        currentLevel = service.levelAfterMonth(currentLevel, month);
        closedMonths.put(month, currentLevel);
        return currentLevel;
    }

    public static UserLevelProgress forNewUser(MonthlyLevelService service){
        return new UserLevelProgress(UserLevel.NOVICE, service);
    }

    public UserLevel currentLevel(){
        return currentLevel;
    }
}
