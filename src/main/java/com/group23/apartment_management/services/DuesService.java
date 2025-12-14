package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Debt;
import com.group23.apartment_management.entities.dto.ApartmentDuesDTO;
import com.group23.apartment_management.repositories.ApartmentRepository;
import com.group23.apartment_management.repositories.DebtRepository;
import com.group23.apartment_management.repositories.DueAmountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DuesService {

    private final ApartmentRepository apartmentRepository;
    private final DebtRepository debtRepository;
    private final DueAmountRepository dueAmountRepository;

    /**
     * TOPLU AİDAT YANSITMA (Tahakkuk İşlemi)
     * Daire tipine göre aidat tutarını belirler ve Debts tablosuna yansıtır.
     */
    public void applyDefinedDuesToDebts(Integer blockId, Integer periodId, Integer debtTypeId) {

        // 1. O Bloktaki Daireleri Tip Bilgisiyle Birlikte Çek (Artık doğru Repository metodu çağrılıyor)
        List<ApartmentDuesDTO> targets = apartmentRepository.findApartmentsForDuesByBlockId(blockId);

        if (targets == null || targets.isEmpty()) return;

        // --- 2. Her daire için tipi kontrol et ve borç tutarını belirle (Kod içi sabit tutar) ---
        for (ApartmentDuesDTO apt : targets) {

            BigDecimal fixedAmount;

            // Daire tipine göre tutarı belirle (Veritabanınızdaki Type ID'leri ile kontrol edin)
            if (apt.getTypeId() == 2) { // Örn: 1+1
                fixedAmount = new BigDecimal("1000.00");
            } else if (apt.getTypeId() == 3) { // Örn: 2+1
                fixedAmount = new BigDecimal("2000.00");
            } else if (apt.getTypeId() == 1) { // Örn: 3+1
                fixedAmount = new BigDecimal("3000.00");
            } else {
                continue;
            }

            // Mükerrer Kayıt Kontrolü
            Debt existingDebt = debtRepository.findByApartmentPeriodType(apt.getApartmentId(), periodId, debtTypeId);

            if (existingDebt == null) {
                // Yoksa yeni borç kaydını oluştur
                Debt debt = new Debt();
                debt.setApartmentId(apt.getApartmentId());
                debt.setPeriodId(periodId);
                debt.setDebtTypeId(debtTypeId);
                debt.setAmount(fixedAmount);

                debtRepository.save(debt);
            }
        }
    }
}