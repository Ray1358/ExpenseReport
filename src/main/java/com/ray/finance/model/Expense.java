package com.ray.finance.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Expense {
    private final String id;
    private final LocalDate date;
    private final BigDecimal amount;
    private final Category category;
    private final String note;

    public Expense(String id, LocalDate date, BigDecimal amount, Category category, String note) {
        this.id = Objects.requireNonNull(id, "id");
        this.date = Objects.requireNonNull(date, "date");
        this.amount = Objects.requireNonNull(amount, "amount");
        this.category = Objects.requireNonNull(category, "category");
        this.note = note == null ? "" : note.trim();
    }

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public BigDecimal getAmount() { return amount; }
    public Category getCategory() { return category; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return String.format("%s | %s | $%s | %s | %s",
                id, date, amount, category, note);
    }
}
