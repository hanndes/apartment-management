package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.*;
import com.group23.apartment_management.entities.dto.ApartmentDropdownDTO;
import com.group23.apartment_management.repositories.BlockRepository; 
import com.group23.apartment_management.repositories.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final DebtService debtService;
    private final ApartmentService apartmentService; 
    private final BlockRepository blockRepository;   

    public List<Expense> getAllExpenses() { return expenseRepository.findAll(); }
    public List<ExpenseCategory> getCategories() { return expenseRepository.findAllCategories(); }
    public void deleteExpense(int id) { expenseRepository.delete(id); }


    public void addExpenseAndDistribute(Expense expense, Integer periodId, Integer debtTypeId) {

        if (expense.getDate() == null) {
            expense.setDate(new java.sql.Date(System.currentTimeMillis()));
        }

        int expenseId = expenseRepository.save(expense);

        if (periodId != null && debtTypeId != null && expense.getBlockId() != null) {

            Block block = blockRepository.findById(expense.getBlockId());

            if (block != null && block.getTotalApartments() > 0) {

                BigDecimal totalAmount = expense.getAmount();
                BigDecimal totalFlatCount = new BigDecimal(block.getTotalApartments());

                BigDecimal sharePerFlat = totalAmount.divide(totalFlatCount, 2, RoundingMode.HALF_UP);

                List<ApartmentDropdownDTO> targets = apartmentService.getApartmentsByBlockId(expense.getBlockId());

                for (ApartmentDropdownDTO apt : targets) {
                    Debt existingDebt = debtService.findByApartmentPeriodAndType(apt.getId(), periodId, debtTypeId);

                    if (existingDebt != null) {
                        debtService.incrementDebtAmountAndRemaining(existingDebt.getId(), sharePerFlat);
                    } else {
                        Debt debt = new Debt();
                        debt.setApartmentId(apt.getId());
                        debt.setPeriodId(periodId);
                        debt.setDebtTypeId(debtTypeId);
                        debt.setAmount(sharePerFlat);
                        debtService.addDebt(debt);
                    }
                }
            }
        }
    }
}