package com.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.contains;

class UserLevelControllerTest {

    @Test
    void returnsAllLevelsInAscendingOrder() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new UserLevelController())
                .build();

        mockMvc.perform(get("/api/levels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[*].code", contains(
                        "NOVICE", "AMATEUR", "PRACTITIONER",
                        "EXPERT", "GURU", "ORACLE"
                )))
                .andExpect(jsonPath("$[0].displayName").value("Профан"))
                .andExpect(jsonPath("$[5].displayName").value("Оракул"));
    }
}
