package com.group23.apartment_management.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Payment;

@Repository
public class PaymentRepository {
    //giriş yapan kullanıcının yaptığı tüm ödemeler
    public List<Payment> findPaymentsByUserId(int userId){
        List<Payment> list = new ArrayList<>();

        String sql = "SELECT p.*" +
                     "FROM Payments p" +
                     "JOIN Residents r ON p.apartment_id = r.apartment_id" +
                     "WHERE r.user_id = ?" +
                     "ORDER BY p.payment_date DESC"; 

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("payment_id"));
                    payment.setDebtId(rs.getInt("debt_id"));
                    int residentId = rs.getInt("resident_id");
                    if (rs.wasNull()) {
                        payment.setResidentId(null);
                    } else {
                        payment.setResidentId(residentId);
                    }

                    BigDecimal amount = rs.getBigDecimal("amount_paid");
                    payment.setAmountPaid(amount);
                    payment.setPaymentDate(rs.getTimestamp("payment_date"));
                    payment.setPaymentMethod(rs.getString("payment_method"));
                    payment.setReferenceNo(rs.getString("reference_no"));
                    payment.setCreatedAt(rs.getTimestamp("created_at"));

                    list.add(payment);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //belirli bir borca ait tüm ödemeler
    public List<Payment> findByDebtId(int debtId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT * FROM Payments WHERE debt_id = ? ORDER BY payment_date";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, debtId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setId(rs.getInt("payment_id"));
                    p.setDebtId(rs.getInt("debt_id"));
                    int residentId = rs.getInt("resident_id");
                    if (rs.wasNull()) {
                        p.setResidentId(null);
                    } else {
                        p.setResidentId(residentId);
                    }

                    p.setAmountPaid(rs.getBigDecimal("amount_paid"));
                    p.setPaymentDate(rs.getTimestamp("payment_date"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setReferenceNo(rs.getString("reference_no"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));

                    list.add(p);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    //yeni ödeme kaydı
    public boolean save(Payment payment) {
        String sql = "INSERT INTO Payments " +
                "(debt_id, resident_id, amount_paid, payment_date, payment_method, reference_no) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, payment.getDebtId());

            if (payment.getResidentId() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, payment.getResidentId());
            }

            ps.setBigDecimal(3, payment.getAmountPaid());
            ps.setTimestamp(4, payment.getPaymentDate());
            ps.setString(5, payment.getPaymentMethod());
            ps.setString(6, payment.getReferenceNo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
// PaymentRepository.java içinde bu metodu bulun ve değiştirin:

    public List<Payment> findRecentPayments(int limit){
        List<Payment> list = new ArrayList<>();

        // DÜZELTME: "TOP " ile "*" arasına 'limit' değişkenini ekliyoruz.
        // Oluşan SQL şuna benzeyecek: SELECT TOP 5 * FROM ...
        String sql = "SELECT TOP " + limit + " * FROM Payments ORDER BY payment_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Payment p = mapRow(rs);
                list.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    // ortak satır-mapping yardımcısı
    private Payment mapRow(ResultSet rs) throws Exception {
        Payment p = new Payment();
        p.setId(rs.getInt("payment_id"));
        p.setDebtId(rs.getInt("debt_id"));

        int residentId = rs.getInt("resident_id");
        if (rs.wasNull()) {
            p.setResidentId(null);
        } else {
            p.setResidentId(residentId);
        }

        p.setAmountPaid(rs.getBigDecimal("amount_paid"));
        p.setPaymentDate(rs.getTimestamp("payment_date"));
        p.setPaymentMethod(rs.getString("payment_method"));
        p.setReferenceNo(rs.getString("reference_no"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}