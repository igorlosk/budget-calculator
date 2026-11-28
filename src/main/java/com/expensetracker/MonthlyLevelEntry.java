package com.expensetracker;

import java.time.YearMonth;

public record MonthlyLevelEntry(YearMonth month, UserLevel level) {}
