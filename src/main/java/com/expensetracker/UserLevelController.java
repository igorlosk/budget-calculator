package com.expensetracker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class UserLevelController {

    @GetMapping("/api/levels")
    public List<UserLevelResponse> levels(){
        return Arrays.stream(UserLevel.values())
                .map(UserLevelResponse::from)
                .toList();
    }
}
