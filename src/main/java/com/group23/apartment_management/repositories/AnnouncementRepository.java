package com.group23.apartment_management.repositories;


import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Announcement;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnnouncementRepository {

    public List<Announcement> findAllActive() {
        List<Announcement> list = new ArrayList<>();

        String sql = "SELECT * FROM Announcements WHERE is_active = 1 ORDER BY created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Announcement ann = new Announcement();
                ann.setId(rs.getInt("announcement_id"));
                ann.setTitle(rs.getString("title"));
                ann.setContent(rs.getString("content"));
                ann.setPriority(rs.getString("priority"));
                ann.setCreatedAt(rs.getTimestamp("created_at"));
                ann.setActive(rs.getBoolean("is_active"));

                list.add(ann);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Announcement> findUrgentActive() {
        List<Announcement> list = new ArrayList<>();

        String sql = "SELECT * FROM Announcements WHERE is_active = 1 AND (priority = 'Acil' OR priority = 'Yüksek') ORDER BY created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Announcement ann = new Announcement();
                ann.setId(rs.getInt("announcement_id"));
                ann.setTitle(rs.getString("title"));
                ann.setContent(rs.getString("content"));
                ann.setPriority(rs.getString("priority"));
                ann.setCreatedAt(rs.getTimestamp("created_at"));
                ann.setActive(rs.getBoolean("is_active"));
                list.add(ann);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
