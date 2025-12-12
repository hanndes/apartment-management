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

    // AnnouncementRepository.java içine ekleyin:

    // YENİ DUYURU KAYDETME
    public boolean save(Announcement announcement) {
        String sql = "INSERT INTO Announcements (title, content, priority, created_at, is_active) VALUES (?, ?, ?, ?, 1)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, announcement.getTitle());
            ps.setString(2, announcement.getContent());
            ps.setString(3, announcement.getPriority());
            ps.setTimestamp(4, announcement.getCreatedAt());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DUYURU SİLME (Soft Delete: is_active = 0 yapar)
    public void delete(int id) {
        String sql = "UPDATE Announcements SET is_active = 0 WHERE announcement_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
