package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    // KULLANICI GİRİŞİ İÇİN (Role ismiyle beraber çeker)
    public User findByEmail(String email) {
        User user = null;

        // Users, UserRoles ve Roles tablolarını birleştiriyoruz
        String sql = "SELECT U.user_id, U.username, U.password, U.email, U.is_active, R.role_name " +
                "FROM Users U " +
                "LEFT JOIN UserRoles UR ON U.user_id = UR.user_id " +
                "LEFT JOIN Roles R ON UR.role_id = R.role_id " +
                "WHERE U.email = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("user_id")); // User.java'daki isimle aynı olmalı
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));

                    // Rol adını direkt String olarak set ediyoruz
                    user.setRole(rs.getString("role_name"));
                    user.setActive(rs.getBoolean("is_active"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return user;
    }

    // ADMİN PANELİNDEKİ LİSTE İÇİN
    public List<User> findAll() {
        List<User> list = new ArrayList<>();

        String sql = "SELECT U.user_id, U.username, U.email, U.is_active, R.role_name " +
                "FROM Users U " +
                "LEFT JOIN UserRoles UR ON U.user_id = UR.user_id " +
                "LEFT JOIN Roles R ON UR.role_id = R.role_id " +
                "ORDER BY U.username";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role_name")); // "ADMIN" veya "RESIDENT" gelir
                u.setActive(rs.getBoolean("is_active"));
                list.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // YENİ KULLANICI EKLEME (Transaction ile hem User hem Role ekler)
    public int save(User user) {
        Connection con = null;
        int newUserId = -1;

        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // İşlem bütünlüğü başlat

            // 1. Kullanıcıyı Ekle
            String sqlUser = "INSERT INTO Users (username, password, email, is_active) VALUES (?, ?, ?, 1)";
            try (PreparedStatement ps = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail()); // Email ekledim, önemli

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) newUserId = rs.getInt(1);
                }
            }

            // 2. Rol ID'sini bul (Gelen string role göre: 'ADMIN' -> 1 gibi)
            int roleId = 2; // Varsayılan RESIDENT olsun
            String sqlFindRole = "SELECT role_id FROM Roles WHERE role_name = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlFindRole)) {
                ps.setString(1, user.getRole());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) roleId = rs.getInt("role_id");
                }
            }

            // 3. UserRoles tablosuna bağla
            if (newUserId != -1) {
                String sqlLink = "INSERT INTO UserRoles (user_id, role_id) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlLink)) {
                    ps.setInt(1, newUserId);
                    ps.setInt(2, roleId);
                    ps.executeUpdate();
                }
            }

            con.commit(); // Her şey yolundaysa kaydet

        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
        } finally {
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (Exception e) {}
        }
        return newUserId;
    }

    // SİLME İŞLEMİ
    public void delete(int id) {
        // Önce UserRoles'dan sil, sonra Users'dan (FK hatası almamak için)
        String sql1 = "DELETE FROM UserRoles WHERE user_id = ?";
        String sql2 = "UPDATE Residents SET user_id = NULL WHERE user_id = ?"; // Sakin bağını kopar
        String sql3 = "DELETE FROM Users WHERE user_id = ?"; // Kullanıcıyı sil

        try (Connection con = DatabaseConnection.getConnection()) {
            // Basitçe sırayla çalıştırıyoruz
            try (PreparedStatement ps = con.prepareStatement(sql1)) { ps.setInt(1, id); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(sql2)) { ps.setInt(1, id); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(sql3)) { ps.setInt(1, id); ps.executeUpdate(); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Resident Bağlantısı
    public void linkUserToResident(int userId, int residentId) {
        String sql = "UPDATE Residents SET user_id = ? WHERE resident_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, residentId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}