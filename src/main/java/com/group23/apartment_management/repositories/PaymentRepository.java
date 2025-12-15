package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Payment;
import com.group23.apartment_management.entities.dto.PaymentDTO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentRepository {

    public List<Payment> findPaymentsByUserId(int userId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.* FROM Payments p " +
                "JOIN Residents r ON p.resident_id = r.resident_id " +
                "WHERE r.user_id = ? ORDER BY p.payment_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setId(rs.getInt("payment_id"));
                    p.setDebtId(rs.getInt("debt_id"));
                    p.setResidentId(rs.getInt("resident_id"));
                    p.setAmountPaid(rs.getBigDecimal("amount_paid"));
                    p.setPaymentDate(rs.getTimestamp("payment_date"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setReferenceNo(rs.getString("reference_no"));
                    try { p.setCreatedAt(rs.getTimestamp("created_at")); } catch (Exception e) { }
                    list.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public List<Payment> findByDebtId(int debtId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM Payments WHERE debt_id = ? ORDER BY payment_date DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, debtId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setId(rs.getInt("payment_id"));
                    p.setDebtId(rs.getInt("debt_id"));
                    p.setResidentId(rs.getInt("resident_id"));
                    p.setAmountPaid(rs.getBigDecimal("amount_paid"));
                    p.setPaymentDate(rs.getTimestamp("payment_date"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setReferenceNo(rs.getString("reference_no"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(p);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Normal Kayıt (Connection kendisi açar)
    public boolean save(Payment payment) {
        String sql = "INSERT INTO Payments (debt_id, resident_id, amount_paid, payment_method, reference_no, payment_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, payment.getDebtId());
            if (payment.getResidentId() != null) {
                ps.setInt(2, payment.getResidentId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(3, payment.getAmountPaid());
            ps.setString(4, payment.getPaymentMethod());
            ps.setString(5, payment.getReferenceNo());
            ps.setTimestamp(6, payment.getPaymentDate());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean save(Connection con, Payment payment) throws java.sql.SQLException {
        String sql = "INSERT INTO Payments (debt_id, resident_id, amount_paid, payment_method, reference_no, payment_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, payment.getDebtId());
            if (payment.getResidentId() != null) {
                ps.setInt(2, payment.getResidentId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(3, payment.getAmountPaid());
            ps.setString(4, payment.getPaymentMethod());
            ps.setString(5, payment.getReferenceNo());
            ps.setTimestamp(6, payment.getPaymentDate());
            return ps.executeUpdate() > 0;
        }
    }

    public List<PaymentDTO> findRecentPayments(int limit) {
        List<PaymentDTO> list = new ArrayList<>();
        String sql = "SELECT TOP (?) p.payment_id, p.amount_paid, p.payment_date, p.payment_method, " +
                "r.first_name, r.last_name, b.block_name, a.door_number " +
                "FROM Payments p " +
                "JOIN Residents r ON p.resident_id = r.resident_id " +
                "JOIN Apartments a ON r.apartment_id = a.apartment_id " +
                "JOIN Blocks b ON a.block_id = b.block_id " +
                "ORDER BY p.payment_date DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PaymentDTO dto = new PaymentDTO();
                dto.setId(rs.getInt("payment_id"));
                dto.setAmount(rs.getBigDecimal("amount_paid"));
                dto.setPaymentDate(rs.getTimestamp("payment_date"));
                dto.setPaymentMethod(rs.getString("payment_method"));
                dto.setUserName(rs.getString("first_name") + " " + rs.getString("last_name"));
                dto.setFlatNumber(rs.getString("block_name") + " D:" + rs.getString("door_number"));
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }


}