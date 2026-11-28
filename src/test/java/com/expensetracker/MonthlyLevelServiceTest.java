package com.expensetracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MonthlyLevelServiceTest {

    private final YearMonth month = YearMonth.of(2027, 2);
    private MonthlyBudgetStore budgetStore;
    private MonthlyLevelService service;
    private ExpenseStore expenseStore;
    private final YearMonth april = month.plusMonths(2);

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

        UserLevel firstClose = progress.closeMonth(month, april);
        UserLevel secondClose = progress.closeMonth(month, april);

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

        assertEquals(UserLevel.PRACTITIONER, progress.closeMonth(month, april));
        assertEquals(UserLevel.EXPERT, progress.closeMonth(march, april));
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

        assertEquals(UserLevel.PRACTITIONER, progress.closeMonth(month, april));
        assertEquals(UserLevel.EXPERT, progress.closeMonth(march, april));
        assertEquals(UserLevel.PRACTITIONER, progress.closeMonth(month, april));
        assertEquals(UserLevel.EXPERT, progress.currentLevel());
    }

    @Test
    void newUserStartsAtNoviceLevel() {
        UserLevelProgress progress = UserLevelProgress.forNewUser(service);

        assertEquals(UserLevel.NOVICE, progress.currentLevel());
    }

    @Test
    void allowsClosingFebruaryOnlyStartingInMarch() {
        budgetStore.save(month, new BigDecimal("250.00"));
        UserLevelProgress progress = UserLevelProgress.forNewUser(service);
        YearMonth march = month.plusMonths(1);

        assertThrows(IllegalArgumentException.class,
                () -> progress.closeMonth(month, month));
        assertEquals(UserLevel.NOVICE, progress.currentLevel());

        assertEquals(UserLevel.AMATEUR, progress.closeMonth(month, march));
    }

    @Test
    void rejectsFutureMonthWhenUsingCurrentDate() {
        UserLevelProgress progress = UserLevelProgress.forNewUser(service);
        YearMonth futureMonth = YearMonth.now().plusYears(1);

        assertThrows(IllegalArgumentException.class,
                () -> progress.closeMonth(futureMonth));

        assertEquals(UserLevel.NOVICE, progress.currentLevel());
    }

    @Test
    void returnsLevelForClosedMonthOnlyAfterClosing() {
        budgetStore.save(month, new BigDecimal("250.00"));
        UserLevelProgress progress = UserLevelProgress.forNewUser(service);

        assertEquals(Optional.empty(), progress.levelForClosedMonth(month));
        assertEquals(UserLevel.NOVICE, progress.currentLevel());

        progress.closeMonth(month, april);

        assertEquals(Optional.of(UserLevel.AMATEUR), progress.levelForClosedMonth(month));
    }

    @Test
    void returnsLevelHistoryNewestMonthFirst(){
        YearMonth march = month.plusMonths(1);
        budgetStore.save(month, new BigDecimal("250.00"));
        budgetStore.save(march, new BigDecimal("100.00"));
        expenseStore.add(new Expense(
                march.atDay(1), new BigDecimal("100"), "Еда", true
        ));

        UserLevelProgress progress = new UserLevelProgress(UserLevel.AMATEUR, service);

        assertEquals(UserLevel.PRACTITIONER, progress.closeMonth(month, march));
        assertEquals(UserLevel.EXPERT, progress.closeMonth(march, april));

        assertEquals(List.of(
                new MonthlyLevelEntry(march, UserLevel.EXPERT),
                new MonthlyLevelEntry(month, UserLevel.PRACTITIONER)
        ), progress.history());
    }
}
