package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Expense;
import com.group23.apartment_management.entities.ExpenseCategory;
import com.group23.apartment_management.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<ExpenseCategory> getCategories() {
        return expenseRepository.findAllCategories();
    }

    public void addExpense(Expense expense) {
        // Tarih boş gelirse bugünü ata
        if (expense.getDate() == null) {
            expense.setDate(new java.sql.Date(System.currentTimeMillis()));
        }
        expenseRepository.save(expense);
    }

    public void deleteExpense(int id) {
        expenseRepository.delete(id);
    }
}