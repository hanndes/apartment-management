package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Debt;
import com.group23.apartment_management.entities.Payment;
import com.group23.apartment_management.entities.dto.PaymentDTO;
import com.group23.apartment_management.repositories.DebtRepository;
import com.group23.apartment_management.repositories.PaymentRepository;
import com.group23.apartment_management.repositories.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DebtRepository debtRepository;       
    private final ResidentRepository residentRepository;


    public List<Payment> getUserPayments(int userId){
        return paymentRepository.findPaymentsByUserId(userId);
    }

    public List<PaymentDTO> getRecentPaymentsForDashboard(int limit) {
        return paymentRepository.findRecentPayments(limit);
    }


    public void processPayment(int debtId, int userId) {

        Debt debt = debtRepository.findById(debtId);

        if (debt == null || debt.isPaid()) {
            return;
        }

    
        Integer residentId = residentRepository.findResidentIdByUserId(userId);
        if (residentId == null) throw new RuntimeException("Sakin bulunamadı!");

        Payment payment = new Payment();
        payment.setDebtId(debtId);
        payment.setResidentId(residentId);

    
        BigDecimal odenecekTutar = debt.getRemainingAmount();
        payment.setAmountPaid(odenecekTutar);

        payment.setPaymentMethod("ONLİNE");
        payment.setReferenceNo("WEB-" + System.currentTimeMillis());
        payment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));

        paymentRepository.save(payment);


        debtRepository.updateRemainingAndStatus(debtId, BigDecimal.ZERO, true);
    }
    // PaymentService.java içine ekleyin

    /**
     * Admin panelinden gelen manuel ödeme işlemini kaydeder ve borç bakiyesini günceller.
     * Kısmi ödemeyi destekler.
     */
    public void processPaymentAmount(int debtId, int residentId, BigDecimal amountPaid, String paymentMethod, String referenceNo) {

        Debt debt = debtRepository.findById(debtId);

        if (debt == null) {
            throw new IllegalArgumentException("Borç kaydı bulunamadı.");
        }
        if (amountPaid.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Ödeme tutarı sıfırdan büyük olmalıdır.");
        }

        BigDecimal remaining = debt.getRemainingAmount();
        BigDecimal newRemaining = remaining.subtract(amountPaid);

        if (newRemaining.compareTo(BigDecimal.ZERO) < 0) {
            // Fazla ödeme yapıldıysa, sadece kalan miktarı düş ve uyar
            // Ya da bu durumda kalan borç 0 olarak set edilmeli.
            newRemaining = BigDecimal.ZERO;
            // İleride, fazla ödemenin cüzdana iadesi gibi bir mantık eklenebilir.
        }

        boolean isPaid = newRemaining.compareTo(BigDecimal.ZERO) == 0;

        // 1. Payment (Ödeme) Kaydını Oluştur
        Payment payment = new Payment();
        payment.setDebtId(debtId);
        payment.setResidentId(residentId);
        payment.setAmountPaid(amountPaid);
        payment.setPaymentMethod(paymentMethod);
        payment.setReferenceNo(referenceNo);
        payment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));

        paymentRepository.save(payment);

        // 2. Debt (Borç) Bakiyesini Güncelle
        debtRepository.updateRemainingAndStatus(debtId, newRemaining, isPaid);
    }
}