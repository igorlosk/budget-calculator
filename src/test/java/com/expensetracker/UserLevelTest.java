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
}
