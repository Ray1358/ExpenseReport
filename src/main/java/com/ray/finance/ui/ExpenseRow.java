package com.ray.finance.ui;

import com.ray.finance.model.Expense;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class ExpenseRow {
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty amount = new SimpleStringProperty();
    private final StringProperty category = new SimpleStringProperty();
    private final StringProperty note = new SimpleStringProperty();

    public ExpenseRow(Expense e) {
        id.set(e.getId());
        date.set(e.getDate().toString());
        amount.set(e.getAmount().toPlainString());
        category.set(e.getCategory().name());
        note.set(e.getNote());
    }

    public StringProperty idProperty() { return id; }
    public StringProperty dateProperty() { return date; }
    public StringProperty amountProperty() { return amount; }
    public StringProperty categoryProperty() { return category; }
    public StringProperty noteProperty() { return note; }
}
