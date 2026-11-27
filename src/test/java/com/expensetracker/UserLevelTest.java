package com.expensetracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserLevelTest {

    @Test
    void promotesAmateurToPractitioner() {
        assertEquals(UserLevel.PRACTITIONER, UserLevel.AMATEUR.promote());
    }

    @Test
    void demoteAmateurToNovice() {
        assertEquals(UserLevel.NOVICE, UserLevel.AMATEUR.demote());
    }

    @Test
    void promotesOracleToOracle() {
        assertEquals(UserLevel.ORACLE, UserLevel.ORACLE.promote());
    }

    @Test
    void demoteNoviceToNovice() {
        assertEquals(UserLevel.NOVICE, UserLevel.NOVICE.demote());
    }

    @Test
    void returnsRussianDisplayNames(){
        assertEquals("Профан", UserLevel.NOVICE.displayName());
        assertEquals("Любитель", UserLevel.AMATEUR.displayName());
        assertEquals("Практик", UserLevel.PRACTITIONER.displayName());
        assertEquals("Эксперт", UserLevel.EXPERT.displayName());
        assertEquals("Гуру", UserLevel.GURU.displayName());
        assertEquals("Оракул", UserLevel.ORACLE.displayName());
    }
}
