package com.expensetracker;

public record UserLevelResponse(String code, String displayName) {
    public static UserLevelResponse from(UserLevel level){
        return new UserLevelResponse(level.name(), level.displayName());
    }
}
