package com.expensetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BudgetApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetApplication.class, args);
    }

    @Bean
    public BudgetCalculator budgetCalculator() {
        return new BudgetCalculator();
    }
}
