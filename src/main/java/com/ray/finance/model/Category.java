package com.ray.finance.model;

public enum Category {
    GROCERIES,
    RENT,
    UTILITIES,
    TRANSPORTATION,
    ENTERTAINMENT,
    HEALTHCARE,
    OTHER;

    public static Category fromString(String value) {
        if (value == null) return OTHER;
        String normalized = value.trim().toUpperCase();
        for (Category c : values()) {
            if (c.name().equals(normalized)) return c;
        }
        return OTHER;
    }
}
