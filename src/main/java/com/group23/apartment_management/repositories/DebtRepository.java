package com.group23.apartment_management.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Debt;

@Repository
public class DebtRepository {

    public List<Debt> findDebtsByUserId(int userId) {
        List<Debt> list = new ArrayList<>();

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
                    Debt debt = new Debt();
                    debt.setId(rs.getInt("debt_id"));
                    debt.setApartmentId(rs.getInt("apartment_id"));
                    debt.setPeriodId(rs.getInt("period_id"));
                    debt.setDebtTypeId(rs.getInt("debt_type_id"));

                    BigDecimal amount = rs.getBigDecimal("amount");
                    BigDecimal remaining = rs.getBigDecimal("remaining_amt");
                    debt.setAmount(amount);
                    debt.setRemainingAmount(remaining);

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
    public Debt findById(int debtId) {
        Debt debt = null;
        String sql = "SELECT * FROM Debts WHERE debt_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, debtId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    debt = new Debt();
                    debt.setId(rs.getInt("debt_id"));
                    debt.setApartmentId(rs.getInt("apartment_id"));
                    debt.setPeriodId(rs.getInt("period_id"));
                    debt.setDebtTypeId(rs.getInt("debt_type_id"));
                    debt.setAmount(rs.getBigDecimal("amount"));
                    debt.setRemainingAmount(rs.getBigDecimal("remaining_amt"));
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
    //tüm borçların toplam tutarı 
    public BigDecimal getTotalDebtAmount(){
        String sql = "SELECT SUM(amount) AS total_amount FROM Debts";
        BigDecimal result = BigDecimal.ZERO;

            try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                BigDecimal val = rs.getBigDecimal("total_amount");
                if (val != null) {
                    result = val;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }   

    //ödenmiş borçların toplam tutarı
    public BigDecimal getTotalPaidDebtAmount(){
        String sql = "SELECT SUM(amount) AS total_paid FROM Debts WHERE is_paid = 1";
        BigDecimal result = BigDecimal.ZERO;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                BigDecimal val = rs.getBigDecimal("total_paid");
                if (val != null) {
                    result = val;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * En az bir borcu ödenmiş olan benzersiz daire sayısı
     * (admin ekrandaki "Ödenen Daire" için)
     */
    public int countPaidApartments(){
        String sql = "SELECT COUNT(DISTINCT apartment_id) AS cnt FROM Debts WHERE is_paid = 1";

        int count = 0;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("cnt");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    //borç kaydı olan toplam benzersiz daire sayısı
    public int countAllApartmentsWithDebt(){
        String sql = "SELECT COUNT(DISTINCT apartment_id) AS cnt FROM Debts";

        int count = 0;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("cnt");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // --- ADMİN İÇİN EKLENEN METODLAR ---

    // 1. Tüm Borçları Listele (İsimlerle Beraber)
    public List<Debt> findAllWithDetails() {
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT d.*, r.first_name, r.last_name, b.block_name, a.door_number, dp.period_name, dt.type_name " +
                "FROM Debts d " +
                "JOIN Apartments a ON d.apartment_id = a.apartment_id " +
                "JOIN Blocks b ON a.block_id = b.block_id " +
                "JOIN Residents r ON r.apartment_id = a.apartment_id " + // Dairede oturan kişi
                "JOIN DuesPeriods dp ON d.period_id = dp.period_id " +
                "JOIN DebtTypes dt ON d.debt_type_id = dt.debt_type_id " +
                "ORDER BY d.created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Debt debt = new Debt();
                debt.setId(rs.getInt("debt_id"));
                debt.setAmount(rs.getBigDecimal("amount"));
                debt.setRemainingAmount(rs.getBigDecimal("remaining_amt"));
                debt.setPaid(rs.getBoolean("is_paid"));
                debt.setCreatedAt(rs.getTimestamp("created_at"));

                // Ekstra Bilgiler (Entity'nizde bu alanlar String olarak tanımlı olmalı)
                debt.setApartmentInfo(rs.getString("block_name") + " - D:" + rs.getString("door_number") + " (" + rs.getString("first_name") + " " + rs.getString("last_name") + ")");
                debt.setPeriodName(rs.getString("period_name"));
                debt.setTypeName(rs.getString("type_name"));

                list.add(debt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Yeni Borç Ekle (Tahakkuk)
    public void save(Debt debt) {
        // İlk kayıtta kalan tutar = asıl tutar, ödendi mi = hayır
        String sql = "INSERT INTO Debts (apartment_id, period_id, debt_type_id, amount, remaining_amt, is_paid) VALUES (?, ?, ?, ?, ?, 0)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, debt.getApartmentId());
            ps.setInt(2, debt.getPeriodId());
            ps.setInt(3, debt.getDebtTypeId());
            ps.setBigDecimal(4, debt.getAmount());
            ps.setBigDecimal(5, debt.getAmount()); // Kalan tutar başlangıçta borç kadardır

            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 3. Borç Sil
    public void delete(int id) {
        String sql = "DELETE FROM Debts WHERE debt_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
