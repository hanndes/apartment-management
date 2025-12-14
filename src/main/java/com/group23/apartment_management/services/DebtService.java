package com.group23.apartment_management.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group23.apartment_management.entities.Debt;
import com.group23.apartment_management.repositories.DebtRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;

    // 1. KULLANICININ BORÇLARI (Sakin Paneli)
    public List<Debt> getUserDebts(int userId){
        return debtRepository.findDebtsByUserId(userId);
    }

    // 2. YENİ BORÇ EKLE (ExpenseService tarafından otomatik çağrılır)
    public void addDebt(Debt debt) {
        debtRepository.save(debt);
    }

    // 3. BORÇ DETAYI GETİR
    public Debt getDebtById(int debtId){
        return debtRepository.findById(debtId);
    }

    // 4. ÖDEME SONRASI GÜNCELLEME
    public void updateDebtAfterPayment(int debtId, BigDecimal newRemaining, Boolean isPaid){
        debtRepository.updateRemainingAndStatus(debtId, newRemaining, isPaid);
    }

    // 5. İSTATİSTİK: TAHSİLAT ORANI
    public double getCurrentCollectionRate(){
        BigDecimal total = debtRepository.getTotalDebtAmount();
        BigDecimal paid = debtRepository.getTotalPaidDebtAmount();

        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        BigDecimal rate = paid
                .divide(total, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return rate.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // 6. İSTATİSTİK: ÖDEME YAPAN DAİRE SAYISI
    public int getPaidFlatCountForCurrentPeriod() {
        return debtRepository.countPaidApartments();
    }

    // 7. İSTATİSTİK: TOPLAM BORÇLU DAİRE SAYISI
    public int getTotalFlatCount() {
        return debtRepository.countAllApartmentsWithDebt();
    }

    // 8. İSTATİSTİK: KASA BAKİYESİ (TOPLAM ÖDENEN)
    public BigDecimal getTotalPaidAmount() {
        return debtRepository.getTotalPaidDebtAmount();
    }

    // 9. TÜM BORÇLARI LİSTELE (Admin Paneli)
    public List<Debt> getAllDebts() {
        return debtRepository.findAllWithDetails();
    }

    // 10. BORÇ SİL
    public void deleteDebt(int id) {
        debtRepository.delete(id);
    }

}