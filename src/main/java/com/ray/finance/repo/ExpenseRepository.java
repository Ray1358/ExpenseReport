package com.ray.finance.repo;

import com.ray.finance.model.Expense;

import java.io.IOException;
import java.util.List;

public interface ExpenseRepository {
    List<Expense> findAll() throws IOException;
    void saveAll(List<Expense> expenses) throws IOException;
}
