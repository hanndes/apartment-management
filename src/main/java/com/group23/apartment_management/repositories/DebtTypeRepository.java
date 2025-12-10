package com.group23.apartment_management.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.DebtType;


@Repository
public class DebtTypeRepository {

    public List<DebtType> findAllAcitve(){
        List <DebtType> list = new ArrayList<>();

        String sql = "SELECT * FROM DebtTypes WHERE is_active = 1 ORDER BY type_name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DebtType dt = new DebtType();
                dt.setId(rs.getInt("debt_type_id"));
                dt.setTypeCode(rs.getString("type_code"));
                dt.setTypeName(rs.getString("type_name"));
                dt.setDescription(rs.getString("type_desc"));
                dt.setActive(rs.getBoolean("is_active"));

                list.add(dt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    public DebtType findByCode(String code) {
        String sql = "SELECT * FROM DebtTypes WHERE type_code = ? AND is_active = 1";
        DebtType dt = null;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dt = new DebtType();
                    dt.setId(rs.getInt("debt_type_id"));
                    dt.setTypeCode(rs.getString("type_code"));
                    dt.setTypeName(rs.getString("type_name"));
                    dt.setDescription(rs.getString("type_desc"));
                    dt.setActive(rs.getBoolean("is_active"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dt;
    }
    
}
