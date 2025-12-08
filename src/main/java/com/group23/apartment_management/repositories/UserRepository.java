package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection; // Senin sınıfın
import com.group23.apartment_management.entities.User;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class UserRepository {

    public User findByEmail(String email) {
        User user = null;

        String sql = "SELECT U.user_id, U.username, U.password, U.email, U.is_active, R.role_name " +
                "FROM Users U " +
                "JOIN UserRoles UR ON U.user_id = UR.user_id " +
                "JOIN Roles R ON UR.role_id = R.role_id " +
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
                    user.setRole(rs.getString("role_name"));

                    // 2. MAPPING DEĞİŞİKLİĞİ: Veritabanındaki 1/0 değerini boolean'a çevirip set ediyoruz
                    user.setActive(rs.getBoolean("is_active"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}