package com.expensetracker;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@SpringBootTest
@AutoConfigureMockMvc

public class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void rejectsNegativeExpenseAndPreservesDailySummary() throws Exception {
        /*
        @SpringBootTest загружает приложение с твоими BudgetController, ExpenseStore и BudgetCalculator.
        @AutoConfigureMockMvc подготавливает MockMvc, а @Autowired передаёт его в поле теста. Поэтому запускать
        BudgetApplication отдельно не требуется.

        Цепочку удобно читать слева направо: perform выполняет запрос, andExpect проверяет результат,
        andReturn даёт доступ к ответу. В первом GET нужен andReturn(), потому что тело ответа пригодится в
        третьем шаге. В POST достаточно проверить статус 400.

        Последняя строка сравнивает два ответа как JSON. JsonCompareMode.STRICT проверяет состав и значения
        полей, при этом порядок полей объекта может различаться. Если сервер вернёт 400, но всё же учтёт
        отрицательную сумму, проверка статуса пройдёт, а сравнение сводок обнаружит ошибку.
         */

        String summaryUrl = "/api/budget/daily-summary";
        String date = "2026-11-03";

        // 1. Запрашиваем сводку и запоминаем ответ.
        String summaryBefore = mockMvc.perform(get(summaryUrl).param("date", date))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 2. Отправляем расход с отрицательной суммой.
        String invalidExpense = """
                {
                  "date": "2026-11-03",
                  "amount": -250,
                  "category": "Транспорт",
                  "mandatory": true
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidExpense))
                .andExpect(status().isBadRequest());

        // 3. Запрашиваем сводку ещё раз и сравниваем с первым ответом.
        mockMvc.perform(get(summaryUrl).param("date", date))
                .andExpect(status().isOk())
                .andExpect(content().json(summaryBefore, JsonCompareMode.STRICT));
    }

    @Test
    void addsExpenseAndUpdatesDailySummary() throws Exception {
        String summaryUrl = "/api/budget/daily-summary";
        String date = "2026-11-04";

        mockMvc.perform(get(summaryUrl).param("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spentToday").value(0));

        String newExpenseRequest = """
                {
                  "date": "2026-11-04",
                  "amount": 250,
                  "category": "Транспорт",
                  "mandatory": true
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newExpenseRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/budget/daily-summary").param("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.spentToday").value(250))
                .andExpect(jsonPath("$.remainingMonthlyBudget").value(27700));
    }

    @Test
    void repeatedPutKeepsSameMonthlyBudget() throws Exception {
        String date = "2027-02-01";
        String budgetUrl = "/api/budgets/2027-02";
        String body = """
                {"amount": 28000}
                """;

        mockMvc.perform(put(budgetUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(put(budgetUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/budget/daily-summary").param("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableAtStartOfDay").value(1000.0))
                .andExpect(jsonPath("$.remainingMonthlyBudget").value(28000));
    }

    @Test
    void rejectsNegativeBudgetAndPreservesExistingPlan() throws Exception {
        String date = "2027-04-01";
        String budgetUrl = "/api/budgets/2027-04";
        String body = """
                {"amount": 30000}
                """;

        String invalidBudget = """
                {"amount": -500}
                """;

        mockMvc.perform(put(budgetUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(put(budgetUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBudget))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/budget/daily-summary").param("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableAtStartOfDay").value(1000.0))
                .andExpect(jsonPath("$.remainingMonthlyBudget").value(30000));

    }

    @Test
    void acceptsZeroMonthlyBudget() throws Exception {
        String date = "2027-05-01";
        String budgetUrl = "/api/budgets/2027-05";

        String body = """
                {"amount": 0}
        """;

        mockMvc.perform(put(budgetUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/budget/daily-summary").param("date", date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableAtStartOfDay").value(0.0))
                .andExpect(jsonPath("$.remainingMonthlyBudget").value(0));
    }

    @Test
    void returnsEmptyMonthlyReportWhenMonthHasNoExpenses() throws Exception {
        mockMvc.perform(get("/api/budget/monthly-report")
                        .param("month", "2026-12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSpent").value(0))
                .andExpect(jsonPath("$.mandatorySpent").value(0))
                .andExpect(jsonPath("$.unmarkedSpent").value(0))
                .andExpect(jsonPath("$.byCategory").isMap())
                .andExpect(jsonPath("$.byCategory").isEmpty());
    }
}
