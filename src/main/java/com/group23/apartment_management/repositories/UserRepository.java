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

    // KULLANICI GİRİŞİ (Login)
    public User findByEmail(String email) {
        User user = null;

        String sql = "SELECT U.user_id, U.username, U.password, U.email, U.phone_number, U.is_active, R.role_name " +
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
                    user.setId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setPhoneNumber(rs.getString("phone_number")); 
                    user.setRole(rs.getString("role_name"));
                    user.setActive(rs.getBoolean("is_active"));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return user;
    }

    // LİSTELEME
    public List<User> findAll() {
        List<User> list = new ArrayList<>();

        String sql = "SELECT U.user_id, U.username, U.email, U.phone_number, U.is_active, R.role_name " +
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
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setRole(rs.getString("role_name"));
                u.setActive(rs.getBoolean("is_active"));
                list.add(u);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // KAYDETME
    public int save(User user) {
        Connection con = null;
        int newUserId = -1;

        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            
            String sqlUser = "INSERT INTO Users (username, password, email, phone_number, is_active) VALUES (?, ?, ?, ?, 1)";
            try (PreparedStatement ps = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPhoneNumber()); 

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) newUserId = rs.getInt(1);
                }
            }

            
            int roleId = 2; 
            String sqlFindRole = "SELECT role_id FROM Roles WHERE role_name = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlFindRole)) {
                ps.setString(1, user.getRole());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) roleId = rs.getInt("role_id");
                }
            }

            if (newUserId != -1) {
                String sqlLink = "INSERT INTO UserRoles (user_id, role_id) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlLink)) {
                    ps.setInt(1, newUserId);
                    ps.setInt(2, roleId);
                    ps.executeUpdate();
                }
            }

            con.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (Exception ex) {}
        } finally {
            try { if (con != null) { con.setAutoCommit(true); con.close(); } } catch (Exception e) {}
        }
        return newUserId;
    }

    
    public void delete(int id) {
        String sql1 = "DELETE FROM UserRoles WHERE user_id = ?";
        String sql2 = "UPDATE Residents SET user_id = NULL WHERE user_id = ?";
        String sql3 = "DELETE FROM Users WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql1)) { ps.setInt(1, id); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(sql2)) { ps.setInt(1, id); ps.executeUpdate(); }
            try (PreparedStatement ps = con.prepareStatement(sql3)) { ps.setInt(1, id); ps.executeUpdate(); }
        } catch (Exception e) { e.printStackTrace(); }
    }

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