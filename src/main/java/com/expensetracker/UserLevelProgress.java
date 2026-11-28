package com.expensetracker;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UserLevelProgress {

    private final MonthlyLevelService service;
    private final Map<YearMonth, UserLevel> closedMonths = new HashMap<>();
    private UserLevel currentLevel;

    public UserLevelProgress(UserLevel initialLevel, MonthlyLevelService service) {
        this.currentLevel = initialLevel;
        this.service = service;
    }

    public UserLevel closeMonth(YearMonth month) {
        return closeMonth(month, YearMonth.now());
    }

    public static UserLevelProgress forNewUser(MonthlyLevelService service) {
        return new UserLevelProgress(UserLevel.NOVICE, service);
    }

    public UserLevel currentLevel() {
        return currentLevel;
    }

    public UserLevel closeMonth(YearMonth month, YearMonth currentMonth) {
        if (!month.isBefore(currentMonth)) {
            throw new IllegalArgumentException("Можно закрыть только завершённый месяц");
        }

        if (closedMonths.containsKey(month)) {
            return closedMonths.get(month);
        }

        currentLevel = service.levelAfterMonth(currentLevel, month);
        closedMonths.put(month, currentLevel);
        return currentLevel;
    }

    public Optional<UserLevel> levelForClosedMonth(YearMonth month) {
        return Optional.ofNullable(closedMonths.get(month));
    }
}
