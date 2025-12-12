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

    //giriş yapan kullanıcının tüm borçlarını getir
    public List<Debt> getUserDebts(int userId){
        return debtRepository.findDebtsByUserId(userId);
    }

    //tek bir borcu id ile getir
    public Debt getDebtById(int debtId){
        return debtRepository.findById(debtId);
    }

    //ödeme sonrası borcu güncelle
    public void updateDebtAfterPayment(int debtId, BigDecimal newRemaining, Boolean isPaid){
        debtRepository.updateRemainingAndStatus(debtId, newRemaining, isPaid);
    }

    //aidat tahsilat oranı (toplam ödenmiş / toplam borç) * 100
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
    /**
     * Ödenen daire sayısı (en az bir borcu ödenmiş daire)
     */
    public int getPaidFlatCountForCurrentPeriod() {
        // şimdilik tüm dönemler üzerinden; istersen period filtresi ekleriz
        return debtRepository.countPaidApartments();
    }

    /**
     * Toplam daire sayısı (borç kaydı olan daireler)
     */
    public int getTotalFlatCount() {
        return debtRepository.countAllApartmentsWithDebt();
    }
    // DebtService içine ekleyin:

    public List<Debt> getAllDebts() {
        return debtRepository.findAllWithDetails();
    }

    public void addDebt(Debt debt) {
        debtRepository.save(debt);
    }

    public void deleteDebt(int id) {
        debtRepository.delete(id);
    }
}
