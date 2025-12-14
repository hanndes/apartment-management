package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.*;
import com.group23.apartment_management.entities.dto.ApartmentDropdownDTO;
import com.group23.apartment_management.repositories.BlockRepository; // Yeni Repository
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
    private final ApartmentService apartmentService; // Daireleri bulmak için
    private final BlockRepository blockRepository;   // Blok bilgisini (Daire sayısını) çekmek için

    public List<Expense> getAllExpenses() { return expenseRepository.findAll(); }
    public List<ExpenseCategory> getCategories() { return expenseRepository.findAllCategories(); }
    public void deleteExpense(int id) { expenseRepository.delete(id); }

    /**
     * Gideri kaydeder ve BLOKTAKİ TANIMLI DAİRE SAYISINA (total_apartments) GÖRE BÖLER.
     */
    public void addExpenseAndDistribute(Expense expense, Integer periodId, Integer debtTypeId) {

        // 1. Tarih kontrolü
        if (expense.getDate() == null) {
            expense.setDate(new java.sql.Date(System.currentTimeMillis()));
        }

        // 2. Gideri Kaydet
        expenseRepository.save(expense);

        // 3. DAĞITIM MANTIĞI
        if (periodId != null && debtTypeId != null && expense.getBlockId() != null) {

            // A) Blok Bilgisini Çek (BlockRepository'den)
            Block block = blockRepository.findById(expense.getBlockId());

            // Blok bulunduysa ve daire sayısı 0'dan büyükse hesapla
            if (block != null && block.getTotalApartments() > 0) {

                // B) TUTAR HESABI: Gider / Bloktaki Toplam Daire Kapasitesi
                BigDecimal totalAmount = expense.getAmount();
                BigDecimal totalFlatCount = new BigDecimal(block.getTotalApartments()); // Veritabanındaki sayı

                // Bölme işlemi (2 basamak hassasiyet)
                BigDecimal sharePerFlat = totalAmount.divide(totalFlatCount, 2, RoundingMode.HALF_UP);

                // C) O bloktaki Mevcut Daireleri Bul
                List<ApartmentDropdownDTO> targets = apartmentService.getApartmentsByBlockId(expense.getBlockId());

                // D) Her daireye hesaplanan payı borç olarak yaz
                for (ApartmentDropdownDTO apt : targets) {
                    Debt debt = new Debt();
                    debt.setApartmentId(apt.getId());
                    debt.setPeriodId(periodId);
                    debt.setDebtTypeId(debtTypeId);
                    debt.setAmount(sharePerFlat); // Kapasiteye göre hesaplanan tutar

                    debtService.addDebt(debt);
                }
            }
        }
    }
}