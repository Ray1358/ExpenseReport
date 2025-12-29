package com.ray.finance.repo;

import com.ray.finance.model.Category;
import com.ray.finance.model.Expense;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class SqliteExpenseRepository implements ExpenseRepository {

    private final String jdbcUrl;

    public SqliteExpenseRepository(Path dbPath) {
        try {
            Files.createDirectories(dbPath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create DB directory: " + e.getMessage(), e);
        }
        // Example: jdbc:sqlite:data/finance.db
        this.jdbcUrl = "jdbc:sqlite:" + dbPath.toString();
        initSchema();
    }

    private void initSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS expenses (
                  id TEXT PRIMARY KEY,
                  date TEXT NOT NULL,
                  amount TEXT NOT NULL,
                  category TEXT NOT NULL,
                  note TEXT NOT NULL
                );
                """;
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init schema: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Expense> findAll() throws IOException {
        String sql = "SELECT id, date, amount, category, note FROM expenses ORDER BY date DESC";
        List<Expense> out = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                LocalDate date = LocalDate.parse(rs.getString("date"));
                BigDecimal amount = new BigDecimal(rs.getString("amount"));
                Category category = Category.fromString(rs.getString("category"));
                String note = rs.getString("note");
                out.add(new Expense(id, date, amount, category, note));
            }
            return out;

        } catch (SQLException e) {
            throw new IOException("DB read failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveAll(List<Expense> expenses) throws IOException {
        // Simple approach: replace all rows (fine for small personal app)
        String delete = "DELETE FROM expenses";
        String insert = "INSERT INTO expenses (id, date, amount, category, note) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            conn.setAutoCommit(false);

            try (Statement st = conn.createStatement()) {
                st.execute(delete);
            }

            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                for (Expense e : expenses) {
                    ps.setString(1, e.getId());
                    ps.setString(2, e.getDate().toString());
                    ps.setString(3, e.getAmount().toPlainString());
                    ps.setString(4, e.getCategory().name());
                    ps.setString(5, e.getNote());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new IOException("DB write failed: " + e.getMessage(), e);
        }
    }
}
