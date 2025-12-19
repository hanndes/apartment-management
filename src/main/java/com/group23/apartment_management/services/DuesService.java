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

    public void applyDefinedDuesToDebts(Integer blockId, Integer periodId, Integer debtTypeId) {

        List<ApartmentDuesDTO> targets = apartmentRepository.findApartmentsForDuesByBlockId(blockId);

        if (targets == null || targets.isEmpty()) return;

        for (ApartmentDuesDTO apt : targets) {

            BigDecimal fixedAmount;

            if (apt.getTypeId() == 2) {
                fixedAmount = new BigDecimal("1000.00");
            } else if (apt.getTypeId() == 3) {
                fixedAmount = new BigDecimal("2000.00");
            } else if (apt.getTypeId() == 1) {
                fixedAmount = new BigDecimal("3000.00");
            } else {
                continue;
            }

            Debt existingDebt = debtRepository.findByApartmentPeriodType(apt.getApartmentId(), periodId, debtTypeId);

            if (existingDebt == null) {
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