package com.ray.finance.ui;

import com.ray.finance.model.Category;
import com.ray.finance.repo.SqliteExpenseRepository;
import com.ray.finance.service.ExpenseService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

public class FinanceFxApp extends Application {

    private ExpenseService service;
    private final ObservableList<ExpenseRow> rows = FXCollections.observableArrayList();

    private final Label totalLabel = new Label("Total: $0.00");
    private final TextArea breakdownArea = new TextArea();

    @Override
    public void start(Stage stage) throws Exception {
        // DB location
        Path dbPath = Path.of("data", "finance.db");
        this.service = new ExpenseService(new SqliteExpenseRepository(dbPath));

        // Table
        TableView<ExpenseRow> table = new TableView<>(rows);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<ExpenseRow, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c -> c.getValue().dateProperty());

        TableColumn<ExpenseRow, String> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(c -> c.getValue().amountProperty());

        TableColumn<ExpenseRow, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(c -> c.getValue().categoryProperty());

        TableColumn<ExpenseRow, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(c -> c.getValue().noteProperty());

        table.getColumns().addAll(dateCol, amtCol, catCol, noteCol);

        // Form inputs
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField amountField = new TextField();
        amountField.setPromptText("12.50");

        ComboBox<Category> categoryBox = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        categoryBox.setValue(Category.OTHER);

        TextField noteField = new TextField();
        noteField.setPromptText("Optional note");

        Button addBtn = new Button("Add Expense");
        Label status = new Label();

        addBtn.setOnAction(e -> {
            try {
                LocalDate date = datePicker.getValue();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());
                Category cat = categoryBox.getValue();
                String note = noteField.getText();

                service.addExpense(date, amount, cat, note);

                // clear inputs
                amountField.clear();
                noteField.clear();
                status.setText("Saved ✅");
                refreshTableAndSummary();

            } catch (Exception ex) {
                status.setText("Error: " + ex.getMessage());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);

        form.add(new Label("Date:"), 0, 0);
        form.add(datePicker, 1, 0);

        form.add(new Label("Amount:"), 0, 1);
        form.add(amountField, 1, 1);

        form.add(new Label("Category:"), 0, 2);
        form.add(categoryBox, 1, 2);

        form.add(new Label("Note:"), 0, 3);
        form.add(noteField, 1, 3);

        form.add(addBtn, 1, 4);
        form.add(status, 1, 5);

        ColumnConstraints left = new ColumnConstraints();
        left.setMinWidth(90);
        ColumnConstraints right = new ColumnConstraints();
        right.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(left, right);

        // Summary controls
        TextField monthField = new TextField(YearMonth.now().toString()); // YYYY-MM
        monthField.setPromptText("YYYY-MM");
        Button refreshBtn = new Button("Refresh");

        breakdownArea.setEditable(false);
        breakdownArea.setPrefRowCount(8);

        refreshBtn.setOnAction(e -> refreshTableAndSummary(monthField.getText().trim()));

        HBox summaryTop = new HBox(10, new Label("Month:"), monthField, refreshBtn);
        summaryTop.setPadding(new Insets(0, 0, 6, 0));

        VBox summaryBox = new VBox(8, summaryTop, totalLabel, new Label("By Category:"), breakdownArea);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 6; -fx-background-radius: 6;");

        VBox leftPane = new VBox(12, form, summaryBox);
        leftPane.setPadding(new Insets(10));
        leftPane.setPrefWidth(360);

        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setCenter(new VBox(10, new Label("Expenses"), table));
        BorderPane.setMargin(root.getCenter(), new Insets(10));

        Scene scene = new Scene(root, 980, 560);
        stage.setTitle("Finance Manager (SQLite + JavaFX)");
        stage.setScene(scene);
        stage.show();

        refreshTableAndSummary();
    }

    private void refreshTableAndSummary() {
        refreshTableAndSummary(YearMonth.now().toString());
    }

    private void refreshTableAndSummary(String monthText) {
        try {
            rows.clear();
            var all = service.listAll();
            for (var e : all) rows.add(new ExpenseRow(e));

            YearMonth month = YearMonth.parse(monthText);
            var total = service.monthlyTotal(month);
            Map<Category, BigDecimal> byCat = service.monthlyByCategory(month);

            totalLabel.setText("Total: $" + total.toPlainString());

            StringBuilder sb = new StringBuilder();
            if (byCat.isEmpty()) {
                sb.append("(No expenses for this month)");
            } else {
                for (var entry : byCat.entrySet()) {
                    sb.append(entry.getKey().name())
                            .append(": $")
                            .append(entry.getValue().toPlainString())
                            .append("\n");
                }
            }
            breakdownArea.setText(sb.toString());

        } catch (Exception ex) {
            breakdownArea.setText("Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
