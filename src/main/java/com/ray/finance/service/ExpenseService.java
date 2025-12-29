package com.ray.finance.service;

import com.ray.finance.model.Category;
import com.ray.finance.model.Expense;
import com.ray.finance.repo.ExpenseRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public final class ExpenseService {
    private final ExpenseRepository repo;

    public ExpenseService(ExpenseRepository repo) {
        this.repo = repo;
    }

    public List<Expense> listAll() throws IOException {
        return repo.findAll();
    }

    public Expense addExpense(LocalDate date, BigDecimal amount, Category category, String note) throws IOException {
        validate(date, amount, category);

        List<Expense> expenses = repo.findAll();
        String id = UUID.randomUUID().toString().substring(0, 8);
        Expense expense = new Expense(id, date, amount, category, note);
        expenses.add(expense);
        repo.saveAll(expenses);
        return expense;
    }

    public BigDecimal monthlyTotal(YearMonth month) throws IOException {
        return repo.findAll().stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(month))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Category, BigDecimal> monthlyByCategory(YearMonth month) throws IOException {
        Map<Category, BigDecimal> map = new EnumMap<>(Category.class);

        for (Expense e : repo.findAll()) {
            if (!YearMonth.from(e.getDate()).equals(month)) continue;
            map.put(e.getCategory(), map.getOrDefault(e.getCategory(), BigDecimal.ZERO).add(e.getAmount()));
        }
        return map;
    }

    public List<Expense> listMonth(YearMonth month) throws IOException {
        return repo.findAll().stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(month))
                .sorted(Comparator.comparing(Expense::getDate).reversed())
                .collect(Collectors.toList());
    }

    private void validate(LocalDate date, BigDecimal amount, Category category) {
        if (date == null) throw new IllegalArgumentException("date is required");
        if (amount == null) throw new IllegalArgumentException("amount is required");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        if (category == null) throw new IllegalArgumentException("category is required");
    }
}
