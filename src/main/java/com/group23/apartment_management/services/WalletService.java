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

    public void payDebtWithWallet(int userId, int debtId) {

        Connection con = null;

        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);
            Wallet wallet = walletRepository.findByUserId(userId);
            if (wallet == null) throw new RuntimeException("Cüzdan bulunamadı!");

            Debt debt = debtRepository.findById(debtId);
            if (debt == null || debt.isPaid()) throw new RuntimeException("Borç zaten ödenmiş.");

            BigDecimal amountToPay = debt.getRemainingAmount();
            if (wallet.getBalance().compareTo(amountToPay) < 0) {
                throw new RuntimeException("Yetersiz Bakiye! Lütfen para yükleyiniz.");
            }

            BigDecimal newBalance = wallet.getBalance().subtract(amountToPay);
            walletRepository.updateBalance(con, wallet.getWalletId(), newBalance);

            walletRepository.logTransaction(con, wallet.getWalletId(), amountToPay.negate(), "PAYMENT", "Borç: " + debtId);

            Payment payment = new Payment();
            payment.setDebtId(debtId);
            payment.setResidentId(wallet.getResidentId());
            payment.setAmountPaid(amountToPay);
            payment.setPaymentMethod("WALLET");
            payment.setReferenceNo("WAL-" + System.currentTimeMillis());
            payment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));
            paymentRepository.save(con, payment);

            debtRepository.updateRemainingAndStatus(con, debtId, BigDecimal.ZERO, true);

            con.commit();

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Ödeme başarısız: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.close(); } catch (Exception ex) { ex.printStackTrace(); }
            }
        }
    }
}