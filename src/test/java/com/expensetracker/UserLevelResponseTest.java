package com.expensetracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserLevelResponseTest {

    @Test
    void createsResponseWithLevelCodeAndRussianName() {
        UserLevelResponse response = UserLevelResponse.from(UserLevel.PRACTITIONER);

        assertEquals("PRACTITIONER", response.code());
        assertEquals("Практик", response.displayName());
    }
}
