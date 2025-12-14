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

    // 1. SAKİN PANELİ: Giriş yapan kullanıcının (User) oturduğu dairenin borçlarını getirir
    public List<Debt> findDebtsByUserId(int userId) {
        List<Debt> list = new ArrayList<>();

        String sql =
                "SELECT d.*, dp.period_name, dt.type_name " +
                        "FROM Debts d " +
                        "JOIN Apartments a ON d.apartment_id = a.apartment_id " +
                        "JOIN Residents r ON r.apartment_id = a.apartment_id " +
                        "JOIN DuesPeriods dp ON d.period_id = dp.period_id " +
                        "JOIN DebtTypes dt ON d.debt_type_id = dt.debt_type_id " +
                        "WHERE r.user_id = ? " +
                        "ORDER BY d.is_paid ASC, d.created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Debt debt = mapRowToDebt(rs);
                    debt.setPeriodName(rs.getString("period_name"));
                    debt.setTypeName(rs.getString("type_name"));
                    list.add(debt);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. ID İLE BORÇ GETİR
    public Debt findById(int debtId) {
        String sql = "SELECT * FROM Debts WHERE debt_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, debtId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDebt(rs);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 3. YENİ BORÇ KAYDET
    public void save(Debt debt) {
        String sql = "INSERT INTO Debts (apartment_id, period_id, debt_type_id, amount, remaining_amt, is_paid) VALUES (?, ?, ?, ?, ?, 0)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, debt.getApartmentId());
            ps.setInt(2, debt.getPeriodId());
            ps.setInt(3, debt.getDebtTypeId());
            ps.setBigDecimal(4, debt.getAmount());
            ps.setBigDecimal(5, debt.getAmount());

            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4. ÖDEME DURUMU GÜNCELLEME (Standart - Tekli İşlem)
    public void updateRemainingAndStatus(int debtId, BigDecimal newRemaining, boolean isPaid) {
        String sql = "UPDATE Debts SET remaining_amt = ?, is_paid = ? WHERE debt_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, newRemaining);
            ps.setBoolean(2, isPaid);
            ps.setInt(3, debtId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4.1. ÖDEME DURUMU GÜNCELLEME (TRANSACTION İÇİN - Connection Parametreli)
    public void updateRemainingAndStatus(Connection con, int debtId, BigDecimal newRemaining, boolean isPaid) throws Exception {
        String sql = "UPDATE Debts SET remaining_amt = ?, is_paid = ? WHERE debt_id = ?";
        // Dikkat: Connection kapatılmaz, dışarıdan gelir.
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, newRemaining);
            ps.setBoolean(2, isPaid);
            ps.setInt(3, debtId);
            ps.executeUpdate();
        }
    }

    // 5. ADMIN PANELİ LİSTESİ
    public List<Debt> findAllWithDetails() {
        List<Debt> list = new ArrayList<>();
        String sql = "SELECT d.*, r.first_name, r.last_name, b.block_name, a.door_number, dp.period_name, dt.type_name " +
                "FROM Debts d " +
                "JOIN Apartments a ON d.apartment_id = a.apartment_id " +
                "JOIN Blocks b ON a.block_id = b.block_id " +
                "LEFT JOIN Residents r ON r.apartment_id = a.apartment_id " +
                "JOIN DuesPeriods dp ON d.period_id = dp.period_id " +
                "JOIN DebtTypes dt ON d.debt_type_id = dt.debt_type_id " +
                "ORDER BY d.created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Debt debt = mapRowToDebt(rs);
                String residentName = (rs.getString("first_name") != null) ?
                        rs.getString("first_name") + " " + rs.getString("last_name") : "Boş Daire";
                debt.setApartmentInfo(rs.getString("block_name") + " - D:" + rs.getString("door_number") + " (" + residentName + ")");
                debt.setPeriodName(rs.getString("period_name"));
                debt.setTypeName(rs.getString("type_name"));
                list.add(debt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void delete(int id) {
        String sql = "DELETE FROM Debts WHERE debt_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- İSTATİSTİKLER ---
    public BigDecimal getTotalDebtAmount(){ return getBigDecimalScalar("SELECT SUM(amount) FROM Debts"); }
    public BigDecimal getTotalPaidDebtAmount(){ return getBigDecimalScalar("SELECT SUM(amount) FROM Debts WHERE is_paid = 1"); }
    public int countPaidApartments(){ return getIntScalar("SELECT COUNT(DISTINCT apartment_id) FROM Debts WHERE is_paid = 1"); }
    public int countAllApartmentsWithDebt(){ return getIntScalar("SELECT COUNT(DISTINCT apartment_id) FROM Debts"); }

    // --- YENİ EKLENEN METODLAR ---
    public Debt findByApartmentPeriodType(int apartmentId, int periodId, int debtTypeId) {
        String sql = "SELECT * FROM Debts WHERE apartment_id = ? AND period_id = ? AND debt_type_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, apartmentId);
            ps.setInt(2, periodId);
            ps.setInt(3, debtTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToDebt(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public void incrementAmountAndRemaining(int debtId, BigDecimal addAmount) {
        String sql = "UPDATE Debts SET amount = amount + ?, remaining_amt = remaining_amt + ? WHERE debt_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, addAmount);
            ps.setBigDecimal(2, addAmount);
            ps.setInt(3, debtId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- YARDIMCI METODLAR ---
    private Debt mapRowToDebt(ResultSet rs) throws java.sql.SQLException {
        Debt debt = new Debt();
        debt.setId(rs.getInt("debt_id"));
        debt.setApartmentId(rs.getInt("apartment_id"));
        debt.setPeriodId(rs.getInt("period_id"));
        debt.setDebtTypeId(rs.getInt("debt_type_id"));
        debt.setAmount(rs.getBigDecimal("amount"));
        debt.setRemainingAmount(rs.getBigDecimal("remaining_amt"));
        debt.setPaid(rs.getBoolean("is_paid"));
        debt.setCreatedAt(rs.getTimestamp("created_at"));
        return debt;
    }

    private BigDecimal getBigDecimalScalar(String sql) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal val = rs.getBigDecimal(1);
                return val != null ? val : BigDecimal.ZERO;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return BigDecimal.ZERO;
    }

    private int getIntScalar(String sql) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    // --- YENİ EKLENEN METODLAR --- (Devamı)

    /**
     * Belirli bir daireye ait, kalan borcu (remaining_amt) sıfırdan büyük olan
     * en eski (period_id veya created_at'e göre) borç kaydını getirir.
     * AdminController'daki manuel ödeme işlemi için gereklidir.
     */
    public Debt findFirstUnpaidDebtByApartmentId(int apartmentId) {
        Debt debt = null;
        // TOP 1 (veya LIMIT 1) kullanarak en eski ödenmemiş borcu bulur.
        String sql = "SELECT TOP 1 * FROM Debts " +
                "WHERE apartment_id = ? AND remaining_amt > 0 " +
                "ORDER BY period_id ASC, created_at ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, apartmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    debt = mapRowToDebt(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return debt;
    }
}