package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.DebtType;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DebtTypeRepository {

    public List<DebtType> findAll() {
        List<DebtType> list = new ArrayList<>();
       
        String sql = "SELECT * FROM DebtTypes ORDER BY type_name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DebtType dt = new DebtType();
                dt.setId(rs.getInt("debt_type_id"));
                dt.setTypeCode(rs.getString("type_code"));
                dt.setTypeName(rs.getString("type_name"));
                list.add(dt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public DebtType findById(int id) {
        DebtType dt = null;
        String sql = "SELECT * FROM DebtTypes WHERE debt_type_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dt = new DebtType();
                    dt.setId(rs.getInt("debt_type_id"));
                    dt.setTypeCode(rs.getString("type_code"));
                    dt.setTypeName(rs.getString("type_name"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return dt;
    }
}