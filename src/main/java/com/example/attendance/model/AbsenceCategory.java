package com.example.attendance.model;

import lombok.Getter;

@Getter
public enum AbsenceCategory {
    PRESENT("В наявності"),
    DUTY("Наряд"),
    SICK("Хворий"),
    INFIRMARY("Санчастина"),
    AWOL("СЗЧ"),
    VACATION("Відпустка"),
    BUSINESS_TRIP("Відрядження"),
    LEAVE("Звільнення"),
    OTHER("Інше");

    private final String displayName;

    AbsenceCategory(String displayName) {
        this.displayName = displayName;
    }

    public static AbsenceCategory parse(String code) {
        if (code == null || code.trim().isEmpty()) {
            return PRESENT;
        }

        String cleaned = code.trim().toLowerCase();

        return switch (cleaned) {
            case "в/н", "вн", "+" -> PRESENT;
            case "н", "н.", "нар", "нар.", "наряд" -> DUTY;
            case "с/ч", "сч", "санчастина" -> INFIRMARY;
            case "сзч" -> AWOL;
            case "хв", "хв.", "хворий" -> SICK;
            case "відр", "відр.", "відрядження" -> BUSINESS_TRIP;
            case "відп", "відп.", "відпустка" -> VACATION;
            case "зв", "з/в", "зв.", "звільнення" -> LEAVE;
            default -> OTHER;
        };
    }
}