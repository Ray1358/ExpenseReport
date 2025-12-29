package com.ray.finance;

import com.ray.finance.model.Category;
import com.ray.finance.model.Expense;
import com.ray.finance.repo.CsvExpenseRepository;
import com.ray.finance.service.ExpenseService;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws Exception {
        Path csvPath = Path.of("data", "expenses.csv");
        ExpenseService service = new ExpenseService(new CsvExpenseRepository(csvPath));

        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1" -> addExpenseFlow(service, sc);
                case "2" -> listAllFlow(service);
                case "3" -> monthlySummaryFlow(service, sc);
                case "q", "Q" -> {
                    System.out.println("Bye 👋");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Finance Manager ===");
        System.out.println("1) Add expense");
        System.out.println("2) List all expenses");
        System.out.println("3) Monthly summary");
        System.out.println("Q) Quit");
        System.out.print("Choose: ");
    }

    private static void addExpenseFlow(ExpenseService service, Scanner sc) {
        try {
            System.out.print("Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(sc.nextLine().trim());

            System.out.print("Amount (e.g., 12.50): ");
            BigDecimal amount = new BigDecimal(sc.nextLine().trim());

            System.out.print("Category " + java.util.Arrays.toString(Category.values()) + ": ");
            Category category = Category.fromString(sc.nextLine());

            System.out.print("Note (optional): ");
            String note = sc.nextLine();

            Expense created = service.addExpense(date, amount, category, note);
            System.out.println("Saved: " + created);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            System.out.println("Expense not saved.");
        }
    }

    private static void listAllFlow(ExpenseService service) {
        try {
            var all = service.listAll();
            if (all.isEmpty()) {
                System.out.println("No expenses yet.");
                return;
            }
            System.out.println("\n--- All Expenses ---");
            for (Expense e : all) System.out.println(e);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void monthlySummaryFlow(ExpenseService service, Scanner sc) {
        try {
            System.out.print("Month (YYYY-MM): ");
            YearMonth month = YearMonth.parse(sc.nextLine().trim());

            var total = service.monthlyTotal(month);
            Map<Category, BigDecimal> byCat = service.monthlyByCategory(month);

            System.out.println("\n--- Summary for " + month + " ---");
            System.out.println("Total: $" + total);

            if (byCat.isEmpty()) {
                System.out.println("No expenses for this month.");
                return;
            }

            System.out.println("\nBy Category:");
            for (var entry : byCat.entrySet()) {
                System.out.println(entry.getKey() + ": $" + entry.getValue());
            }

            System.out.println("\nRecent Expenses:");
            for (Expense e : service.listMonth(month)) {
                System.out.println(e);
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
}
