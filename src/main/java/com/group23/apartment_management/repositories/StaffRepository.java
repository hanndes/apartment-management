package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Staff;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StaffRepository {

    public List<Staff> findAllActive() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM Staff WHERE is_active = 1 ORDER BY first_name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Staff s = new Staff();
                s.setStaffId(rs.getInt("staff_id"));
                s.setFirstName(rs.getString("first_name"));
                s.setLastName(rs.getString("last_name"));
                s.setRole(rs.getString("role"));
                s.setPhoneNumber(rs.getString("phone_number"));
                s.setSalary(rs.getBigDecimal("salary"));
                s.setStartDate(rs.getDate("start_date"));
                s.setActive(rs.getBoolean("is_active"));
                list.add(s);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean save(Staff staff) {
        String sql = "INSERT INTO Staff (first_name, last_name, role, phone_number, salary, start_date, is_active) VALUES (?, ?, ?, ?, ?, GETDATE(), 1)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, staff.getFirstName());
            ps.setString(2, staff.getLastName());
            ps.setString(3, staff.getRole());
            ps.setString(4, staff.getPhoneNumber());
            ps.setBigDecimal(5, staff.getSalary());

            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public void delete(int id) {
        String sql = "UPDATE Staff SET is_active = 0 WHERE staff_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}