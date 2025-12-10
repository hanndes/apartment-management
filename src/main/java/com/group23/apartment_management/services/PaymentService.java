package com.group23.apartment_management.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group23.apartment_management.entities.Payment;
import com.group23.apartment_management.repositories.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;

    //kullanicinin yaptigi tüm ödemeleri getir "ödemelerim" ekranı için
    public List<Payment> getUserPayments(int userId){
        return paymentRepository.findPaymentsByUserId(userId);
    }

    //belirli bir borca ait ödemeleri getir
    public List<Payment> getPaymentsByDebtId(int debtId){
        return paymentRepository.findByDebtId(debtId);
    }

    //yeni ödeme kaydı
    public boolean addPayment(Payment payment){
        return paymentRepository.save(payment);
    }
}
