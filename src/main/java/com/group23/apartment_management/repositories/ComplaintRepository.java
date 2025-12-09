package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Complaint;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ComplaintRepository {

    public boolean save(Complaint complaint) {
        String sql = "INSERT INTO Complaints (user_id, title, description, category, priority, status) VALUES (?, ?, ?, ?, ?, 'Bekliyor')";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, complaint.getUserId());
            ps.setString(2, complaint.getTitle());
            ps.setString(3, complaint.getDescription());
            ps.setString(4, complaint.getCategory());
            ps.setString(5, complaint.getPriority());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Complaint> findByUserId(int userId) {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM Complaints WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Complaint c = new Complaint();
                    c.setId(rs.getInt("complaint_id"));
                    c.setUserId(rs.getInt("user_id"));
                    c.setTitle(rs.getString("title"));
                    c.setDescription(rs.getString("description"));
                    c.setCategory(rs.getString("category"));
                    c.setStatus(rs.getString("status"));
                    c.setPriority(rs.getString("priority"));
                    c.setAdminResponse(rs.getString("admin_response"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    c.setResolvedAt(rs.getTimestamp("resolved_at"));

                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}