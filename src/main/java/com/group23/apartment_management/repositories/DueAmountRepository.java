package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.DueAmount;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DueAmountRepository {

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
            e.printStackTrace();
        }
        return list;
    }

    private DueAmount mapRowToDueAmount(ResultSet rs) throws java.sql.SQLException {
        DueAmount da = new DueAmount();

        da.setDueId(rs.getInt("due_id"));
        da.setApartmentTypeId(rs.getInt("apartment_type_id"));
        da.setPeriodId(rs.getInt("period_id"));
        da.setDebtTypeId(rs.getInt("debt_type_id"));

        da.setAmount(rs.getBigDecimal("amount"));

        return da;
    }

}