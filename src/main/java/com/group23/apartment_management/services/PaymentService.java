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
    private final DebtRepository debtRepository;       // Borcu güncellemek için şart
    private final ResidentRepository residentRepository;

    // ... (getUserPayments, getRecentPaymentsForDashboard metodları aynen kalıyor) ...
    public List<Payment> getUserPayments(int userId){
        return paymentRepository.findPaymentsByUserId(userId);
    }

    public List<PaymentDTO> getRecentPaymentsForDashboard(int limit) {
        return paymentRepository.findRecentPayments(limit);
    }

    // --- BORÇTAN DÜŞME MANTIĞI BURADA ---
    public void processPayment(int debtId, int userId) {

        // 1. Mevcut Borcu Bul
        Debt debt = debtRepository.findById(debtId);

        // Zaten ödenmişse işlem yapma
        if (debt == null || debt.isPaid()) {
            return;
        }

        // 2. Ödeyen Sakini Bul
        Integer residentId = residentRepository.findResidentIdByUserId(userId);
        if (residentId == null) throw new RuntimeException("Sakin bulunamadı!");

        // 3. Ödemeyi Kaydet (Payments Tablosuna)
        Payment payment = new Payment();
        payment.setDebtId(debtId);
        payment.setResidentId(residentId);

        // Kalan tutarın tamamını ödüyor varsayıyoruz
        BigDecimal odenecekTutar = debt.getRemainingAmount();
        payment.setAmountPaid(odenecekTutar);

        payment.setPaymentMethod("ONLİNE");
        payment.setReferenceNo("WEB-" + System.currentTimeMillis());
        payment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));

        paymentRepository.save(payment);

        // 4. KRİTİK NOKTA: Borcu Güncelle (Debts Tablosundan Düş)
        // Kalan tutar artık 0.00 TL, Durum: Ödendi (true)
        debtRepository.updateRemainingAndStatus(debtId, BigDecimal.ZERO, true);
    }
}