package com.group23.apartment_management.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.DuesPeriod;

@Repository
public class DuesPeriodRepository {
    
    public List<DuesPeriod> findAll(){
        List<DuesPeriod> list = new ArrayList<>();

                String sql = "SELECT * FROM DuesPeriods ORDER BY year DESC, month DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DuesPeriod p = new DuesPeriod();
                p.setId(rs.getInt("period_id"));
                p.setPeriodName(rs.getString("period_name"));
                p.setYear(rs.getInt("year"));
                p.setMonth(rs.getInt("month"));
                p.setDueDate(rs.getTimestamp("due_date"));
                p.setClosed(rs.getBoolean("is_closed"));

                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public DuesPeriod findById(int id) {
        String sql = "SELECT * FROM DuesPeriods WHERE period_id = ?";
        DuesPeriod p = null;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new DuesPeriod();
                    p.setId(rs.getInt("period_id"));
                    p.setPeriodName(rs.getString("period_name"));
                    p.setYear(rs.getInt("year"));
                    p.setMonth(rs.getInt("month"));
                    p.setDueDate(rs.getTimestamp("due_date"));
                    p.setClosed(rs.getBoolean("is_closed"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }
}
