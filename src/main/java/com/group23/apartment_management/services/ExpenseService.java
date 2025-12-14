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

    
    /**
     * Gideri kaydeder ve BLOKTAKİ tanımlı daire sayısına (total_apartments) göre paylaştırır.
     * Eğer hedef daire için aynı dönemde aynı borç türünden bir borç zaten varsa:
     *   - Mevcut borcun amount ve remaining_amt alanları artırılır (merge).
     * Eğer yoksa:
     *   - Yeni bir Debt kaydı oluşturulur.
     */
    public void addExpenseAndDistribute(Expense expense, Integer periodId, Integer debtTypeId) {

        if (expense.getDate() == null) {
            expense.setDate(new java.sql.Date(System.currentTimeMillis()));
        }

        // Gider kaydedilir (önce expense kaydı)
        int expenseId = expenseRepository.save(expense); // dönen id opsiyonel olarak kullanılabilir

        // Eğer dağıtılacaksa ve gerekli parametreler varsa işlemleri yap
        if (periodId != null && debtTypeId != null && expense.getBlockId() != null) {

            Block block = blockRepository.findById(expense.getBlockId());

            if (block != null && block.getTotalApartments() > 0) {

                BigDecimal totalAmount = expense.getAmount();
                BigDecimal totalFlatCount = new BigDecimal(block.getTotalApartments());

                // Daire başına düşen pay
                BigDecimal sharePerFlat = totalAmount.divide(totalFlatCount, 2, RoundingMode.HALF_UP);

                List<ApartmentDropdownDTO> targets = apartmentService.getApartmentsByBlockId(expense.getBlockId());

                // Debug: toplam pay ile totalAmount uyumlu mu diye kontrol (isteğe bağlı/log)
                // BigDecimal checkTotal = sharePerFlat.multiply(totalFlatCount).setScale(2, RoundingMode.HALF_UP);

                for (ApartmentDropdownDTO apt : targets) {
                    // Önce mevcut borcu ara (aynı apartmentId, periodId, debtTypeId)
                    Debt existingDebt = debtService.findByApartmentPeriodAndType(apt.getId(), periodId, debtTypeId);

                    if (existingDebt != null) {
                        // Eğer mevcut varsa: amount ve remaining_amt'ı arttır
                        debtService.incrementDebtAmountAndRemaining(existingDebt.getId(), sharePerFlat);
                    } else {
                        // Yoksa yeni borç oluştur
                        Debt debt = new Debt();
                        debt.setApartmentId(apt.getId());
                        debt.setPeriodId(periodId);
                        debt.setDebtTypeId(debtTypeId);
                        debt.setAmount(sharePerFlat);
                        // remaining_amt default olarak amount ile set edilsin (save metodu buna göre ayarlanmıştır)
                        debtService.addDebt(debt);
                    }
                }
            }
        }
    }
}