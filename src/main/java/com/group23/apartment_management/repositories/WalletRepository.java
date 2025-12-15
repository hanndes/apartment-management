package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Wallet;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class WalletRepository {

    public Wallet findByUserId(int userId) {
        String sql = "SELECT w.* FROM Wallets w " +
                "JOIN Residents r ON w.resident_id = r.resident_id " +
                "WHERE r.user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Wallet(
                            rs.getInt("wallet_id"),
                            rs.getInt("resident_id"),
                            rs.getBigDecimal("balance"),
                            rs.getTimestamp("last_updated")
                    );
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void updateBalance(int walletId, BigDecimal newBalance) {
        String sql = "UPDATE Wallets SET balance = ?, last_updated = GETDATE() WHERE wallet_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, walletId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void logTransaction(int walletId, BigDecimal amount, String type, String desc) {
        String sql = "INSERT INTO WalletTransactions (wallet_id, amount, trx_type, description) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            ps.setBigDecimal(2, amount);
            ps.setString(3, type);
            ps.setString(4, desc);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    // MEVCUT METODUN AYNISI AMA 'Connection' PARAMETRESİ ALIYOR
    public void updateBalance(Connection con, int walletId, BigDecimal newBalance) throws Exception {
        String sql = "UPDATE Wallets SET balance = ?, last_updated = GETDATE() WHERE wallet_id = ?";
        // Dikkat: Burada 'try (Connection...)' YOK. Dışarıdan gelen 'con' kullanılıyor.
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, walletId);
            ps.executeUpdate();
        }
    }

    public void logTransaction(Connection con, int walletId, BigDecimal amount, String type, String desc) throws Exception {
        String sql = "INSERT INTO WalletTransactions (wallet_id, amount, trx_type, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, walletId);
            ps.setBigDecimal(2, amount);
            ps.setString(3, type);
            ps.setString(4, desc);
            ps.executeUpdate();
        }
    }
}