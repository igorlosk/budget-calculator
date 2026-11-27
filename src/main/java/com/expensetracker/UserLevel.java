package com.expensetracker;

public enum UserLevel {
    NOVICE("Профан"),
    AMATEUR("Любитель"),
    PRACTITIONER("Практик"),
    EXPERT("Эксперт"),
    GURU("Гуру"),
    ORACLE("Оракул");

    private final String displayName;

    UserLevel(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public UserLevel promote() {
        return switch (this) {
            case NOVICE -> AMATEUR;
            case AMATEUR -> PRACTITIONER;
            case PRACTITIONER -> EXPERT;
            case EXPERT -> GURU;
            case GURU -> ORACLE;
            case ORACLE -> ORACLE;
        };
    }

    public UserLevel demote() {
        return switch (this) {
            case ORACLE -> GURU;
            case GURU -> EXPERT;
            case EXPERT -> PRACTITIONER;
            case PRACTITIONER -> AMATEUR;
            case AMATEUR -> NOVICE;
            case NOVICE -> NOVICE;
        };
    }
}