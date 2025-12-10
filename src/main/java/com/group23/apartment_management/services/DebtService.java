package com.group23.apartment_management.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.group23.apartment_management.entities.Debts;
import com.group23.apartment_management.repositories.DebtRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebtService {
    
    private final DebtRepository debtRepository;

    //giriş yapan kullanıcının tüm borçlarını getir
    public List<Debts> getUserDebts(int userId){
        return debtRepository.findDebtsByUserId(userId);
    }

    //tek bir borcu id ile getir
    public Debts getDebtById(int debtId){
        return debtRepository.findById(debtId);
    }

    //ödeme sonrası borcu güncelle
    public void updateDebtAfterPayment(int debtId, BigDecimal newRemaining, Boolean isPaid){
        debtRepository.updateRemainingAndStatus(debtId, newRemaining, isPaid);
    }
}
