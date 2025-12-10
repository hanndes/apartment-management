package com.group23.apartment_management.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Debts;

@Repository
public class DebtRepository {

    public List<Debts> findDebtsByUserId(int userId) {
        List<Debts> list = new ArrayList<>();

        String sql =
                "SELECT d.* " +
                "FROM Debts d " +
                "JOIN Apartments a ON d.apartment_id = a.apartment_id " +
                "JOIN Residents r ON r.apartment_id = a.apartment_id " +
                "WHERE r.user_id = ? " +
                "ORDER BY d.created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Debts debt = new Debts();
                    debt.setId(rs.getInt("debt_id"));
                    debt.setApartmentId(rs.getInt("apartment_id"));
                    debt.setPeriodId(rs.getInt("period_id"));
                    debt.setDebtTypeId(rs.getInt("debt_type_id"));

                    BigDecimal amount = rs.getBigDecimal("amount");
                    BigDecimal remaining = rs.getBigDecimal("remaining_amt");
                    debt.setAmount(amount);
                    debt.setRemainingAmt(remaining);

                    debt.setPaid(rs.getBoolean("is_paid"));
                    debt.setCreatedAt(rs.getTimestamp("createdAt"));

                    list.add(debt);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tek bir borcu ID ile getir (detay sayfası/özet için kullanılabilir)
     */
    public Debts findById(int debtId) {
        Debts debt = null;
        String sql = "SELECT * FROM Debts WHERE debt_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, debtId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    debt = new Debts();
                    debt.setId(rs.getInt("debt_id"));
                    debt.setApartmentId(rs.getInt("apartment_id"));
                    debt.setPeriodId(rs.getInt("period_id"));
                    debt.setDebtTypeId(rs.getInt("debt_type_id"));
                    debt.setAmount(rs.getBigDecimal("amount"));
                    debt.setRemainingAmt(rs.getBigDecimal("remaining_amt"));
                    debt.setPaid(rs.getBoolean("is_paid"));
                    debt.setCreatedAt(rs.getTimestamp("created_at"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return debt;
    }

    /**
     * Ödeme yapıldıktan sonra kalan tutarı ve is_paid alanını güncellemek için
     */
    public void updateRemainingAndStatus(int debtId, BigDecimal newRemaining, boolean isPaid) {
        String sql = "UPDATE Debts SET remaining_amt = ?, is_paid = ? WHERE debt_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, newRemaining);
            ps.setBoolean(2, isPaid);
            ps.setInt(3, debtId);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
