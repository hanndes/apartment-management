package com.group23.apartment_management.services;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Debt;
import com.group23.apartment_management.entities.Payment;
import com.group23.apartment_management.entities.Wallet;
import com.group23.apartment_management.repositories.DebtRepository;
import com.group23.apartment_management.repositories.PaymentRepository;
import com.group23.apartment_management.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final DebtRepository debtRepository;
    private final PaymentRepository paymentRepository;

    public Wallet getUserWallet(int userId) {
        return walletRepository.findByUserId(userId);
    }

    // MANUEL TRANSACTION (Eski Usul - Çok Güvenli)
    public void payDebtWithWallet(int userId, int debtId) {

        Connection con = null;

        try {
            // 1. BAĞLANTIYI AÇ VE KİLİTLE
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // Transaction Başladı!

            // --- İŞLEMLER ---

            // A) Cüzdanı ve Borcu Getir
            Wallet wallet = walletRepository.findByUserId(userId);
            if (wallet == null) throw new RuntimeException("Cüzdan bulunamadı!");

            Debt debt = debtRepository.findById(debtId);
            if (debt == null || debt.isPaid()) throw new RuntimeException("Borç zaten ödenmiş.");

            BigDecimal amountToPay = debt.getRemainingAmount();
            if (wallet.getBalance().compareTo(amountToPay) < 0) {
                throw new RuntimeException("Yetersiz Bakiye! Lütfen para yükleyiniz.");
            }

            // B) Cüzdanı Güncelle (Aynı 'con' ile)
            BigDecimal newBalance = wallet.getBalance().subtract(amountToPay);
            walletRepository.updateBalance(con, wallet.getWalletId(), newBalance);

            // C) Log At (Aynı 'con' ile)
            walletRepository.logTransaction(con, wallet.getWalletId(), amountToPay.negate(), "PAYMENT", "Borç: " + debtId);

            // D) Ödemeyi Kaydet (Aynı 'con' ile)
            Payment payment = new Payment();
            payment.setDebtId(debtId);
            payment.setResidentId(wallet.getResidentId());
            payment.setAmountPaid(amountToPay);
            payment.setPaymentMethod("WALLET");
            payment.setReferenceNo("WAL-" + System.currentTimeMillis());
            payment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));
            paymentRepository.save(con, payment);

            // E) Borcu Kapat (Aynı 'con' ile)
            debtRepository.updateRemainingAndStatus(con, debtId, BigDecimal.ZERO, true);

            // --- BİTİŞ ---
            con.commit(); // Hepsini Veritabanına Yaz!

        } catch (Exception e) {
            // HATA VARSA GERİ AL
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Ödeme başarısız: " + e.getMessage());
        } finally {
            // BAĞLANTIYI KAPAT
            if (con != null) {
                try { con.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }
}