package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.DueAmount;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Repository
public class DueAmountRepository {

    /**
     * Belirli bir döneme ait tanımlanmış aidat şablonlarını (DueAmounts) getirir.
     * Bu metot, DuesService tarafından borç tutarlarını belirlemek için kullanılır.
     * * @param periodId Aranacak dönem ID'si.
     * @return List<DueAmount> Belirtilen döneme ait tüm daire tipi aidatları.
     */
    public List<DueAmount> findByPeriodId(int periodId) {
        List<DueAmount> list = new ArrayList<>();

        // Sorgu: DueAmounts tablosundan gerekli tüm alanları çek
        String sql = "SELECT due_id, apartment_type_id, period_id, debt_type_id, amount " +
                "FROM DueAmounts WHERE period_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, periodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DueAmount da = mapRowToDueAmount(rs);
                    list.add(da);
                }
            }
        } catch (Exception e) {
            // Aidat tanımlaması bulunamaması yaygın bir durumdur, ancak hata kaydı tutulabilir.
            e.printStackTrace();
        }
        return list;
    }

    // --- YARDIMCI METOT: ResultSet'i DueAmount objesine dönüştürür ---
    private DueAmount mapRowToDueAmount(ResultSet rs) throws java.sql.SQLException {
        DueAmount da = new DueAmount();

        // ResultSet'ten verileri çek ve Entity'ye set et
        da.setDueId(rs.getInt("due_id"));
        da.setApartmentTypeId(rs.getInt("apartment_type_id"));
        da.setPeriodId(rs.getInt("period_id"));
        da.setDebtTypeId(rs.getInt("debt_type_id"));

        // Tutar tipi DECIMAL olduğu için BigDecimal olarak çekilmeli
        da.setAmount(rs.getBigDecimal("amount"));

        return da;
    }

    // NOT: Save, findAll gibi metotlar da buraya eklenebilir, ancak tahakkuk için şimdilik sadece findByPeriodId yeterlidir.
}