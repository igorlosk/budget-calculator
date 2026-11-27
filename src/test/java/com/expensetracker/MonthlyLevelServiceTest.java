package com.expensetracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MonthlyLevelServiceTest {

    private final YearMonth month = YearMonth.of(2027, 2);
    private MonthlyBudgetStore budgetStore;
    private MonthlyLevelService service;
    private ExpenseStore expenseStore;

    @BeforeEach
    void setUp() {
        budgetStore = new MonthlyBudgetStore();
        expenseStore = new ExpenseStore();

        expenseStore.add(new Expense(
                month.atDay(1),
                new BigDecimal("250"),
                "Транспорт",
                true
        ));

        service = new MonthlyLevelService(
                budgetStore,
                expenseStore,
                new BudgetCalculator(),
                new UserLevelCalculator()
        );
    }

    @Test
    void keepsLevelWhenMonthlyBudgetIsMissing() {
        UserLevel result = service.levelAfterMonth(UserLevel.AMATEUR, month);

        assertEquals(UserLevel.AMATEUR, result);
    }

    @Test
    void demotesWhenZeroBudgetIsExceeded() {
        budgetStore.save(month, BigDecimal.ZERO);

        UserLevel result = service.levelAfterMonth(UserLevel.AMATEUR, month);

        assertEquals(UserLevel.NOVICE, result);
    }

    @Test
    void promotesWhenSpendingMatchesBudgetForRequestedMonth() {
        budgetStore.save(month, new BigDecimal("250.00"));
        expenseStore.add(new Expense(month.plusMonths(1).atDay(1),
                new BigDecimal("100"), "Food", true));

        UserLevel userLevel = service.levelAfterMonth(UserLevel.AMATEUR, month);

        assertEquals(UserLevel.PRACTITIONER, userLevel);
    }

    @Test
    void closingSameMonthTwiceKeepsLevelFromFirstClose() {
        budgetStore.save(month, new BigDecimal("250.00"));
        UserLevelProgress progress = new UserLevelProgress(UserLevel.AMATEUR, service);

        UserLevel firstClose = progress.closeMonth(month);
        UserLevel secondClose = progress.closeMonth(month);

        assertEquals(UserLevel.PRACTITIONER, firstClose);
        assertEquals(UserLevel.PRACTITIONER, secondClose);
    }

    @Test
    void closingTwoSuccessfulMonthsPromotesTwice() {
        YearMonth march = month.plusMonths(1);
        budgetStore.save(month, new BigDecimal("250.00"));
        budgetStore.save(march, new BigDecimal("100.00"));
        expenseStore.add(new Expense(
                march.atDay(1), new BigDecimal("100"), "Еда", true
        ));

        UserLevelProgress progress = new UserLevelProgress(UserLevel.AMATEUR, service);

        assertEquals(UserLevel.PRACTITIONER, progress.closeMonth(month));
        assertEquals(UserLevel.EXPERT, progress.closeMonth(march));
    }

    @Test
    void reclosingFebruaryAfterMarchPreservesCurrentLevel(){
        YearMonth march = month.plusMonths(1);
        budgetStore.save(month, new BigDecimal("250.00"));
        budgetStore.save(march, new BigDecimal("100.00"));
        expenseStore.add(new Expense(
                march.atDay(1), new BigDecimal("100"), "Еда", true
        ));

        UserLevelProgress progress = new UserLevelProgress(UserLevel.AMATEUR, service);

        assertEquals(UserLevel.PRACTITIONER, progress.closeMonth(month));
        assertEquals(UserLevel.EXPERT, progress.closeMonth(march));
        assertEquals(UserLevel.PRACTITIONER, progress.closeMonth(month));
        assertEquals(UserLevel.EXPERT, progress.currentLevel());
    }

    @Test
    void newUserStartsAtNoviceLevel() {
        UserLevelProgress progress = UserLevelProgress.forNewUser(service);

        assertEquals(UserLevel.NOVICE, progress.currentLevel());
    }
}
