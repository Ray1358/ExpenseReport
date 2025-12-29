package com.ray.finance.repo;

import com.ray.finance.model.Category;
import com.ray.finance.model.Expense;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CsvExpenseRepository implements ExpenseRepository {
    private final Path csvPath;

    public CsvExpenseRepository(Path csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<Expense> findAll() throws IOException {
        ensureFileExistsWithHeader();

        List<Expense> expenses = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) { // header
                    isFirst = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                // id,date,amount,category,note
                String[] parts = splitCsvLine(line);
                if (parts.length < 5) continue;

                String id = parts[0];
                LocalDate date = LocalDate.parse(parts[1]);
                BigDecimal amount = new BigDecimal(parts[2]);
                Category category = Category.fromString(parts[3]);
                String note = parts[4];

                expenses.add(new Expense(id, date, amount, category, note));
            }
        }
        return expenses;
    }

    @Override
    public void saveAll(List<Expense> expenses) throws IOException {
        ensureFileExistsWithHeader();

        try (BufferedWriter writer = Files.newBufferedWriter(csvPath)) {
            writer.write("id,date,amount,category,note");
            writer.newLine();
            for (Expense e : expenses) {
                writer.write(toCsvLine(e));
                writer.newLine();
            }
        }
    }

    private void ensureFileExistsWithHeader() throws IOException {
        if (Files.exists(csvPath)) return;
        Files.createDirectories(csvPath.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(csvPath)) {
            writer.write("id,date,amount,category,note");
            writer.newLine();
        }
    }

    private String toCsvLine(Expense e) {
        // escape quotes in note and wrap note in quotes
        String safeNote = e.getNote().replace("\"", "\"\"");
        return String.join(",",
                e.getId(),
                e.getDate().toString(),
                e.getAmount().toPlainString(),
                e.getCategory().name(),
                "\"" + safeNote + "\""
        );
    }

    private String[] splitCsvLine(String line) {
        // Simple CSV split that handles the last field being quoted (note)
        // Expected: 4 commas total.
        // id,date,amount,category,"note..."
        List<String> out = new ArrayList<>(5);

        int firstQuote = line.indexOf('"');
        if (firstQuote == -1) {
            // no quotes -> split normally
            String[] parts = line.split(",", -1);
            for (String p : parts) out.add(p);
            while (out.size() < 5) out.add("");
            return out.toArray(new String[0]);
        }

        String before = line.substring(0, firstQuote);
        String quoted = line.substring(firstQuote);

        String[] firstParts = before.split(",", -1);
        for (String p : firstParts) out.add(p);

        // remove surrounding quotes if present
        String note = quoted;
        if (note.startsWith("\"") && note.endsWith("\"") && note.length() >= 2) {
            note = note.substring(1, note.length() - 1);
        }
        note = note.replace("\"\"", "\""); // unescape
        out.add(note);

        while (out.size() < 5) out.add("");
        return out.toArray(new String[0]);
    }
}
