package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Resident;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ResidentRepository {

    // Tüm Sakinleri Getir (Ekranda listelemek için)
    public List<Resident> findAllResidents() {
        List<Resident> list = new ArrayList<>();
        String sql = """
            SELECT r.*, 
                   b.block_name, a.door_number, rt.type_name
            FROM Residents r
            LEFT JOIN Apartments a ON r.apartment_id = a.apartment_id
            LEFT JOIN Blocks b ON a.block_id = b.block_id
            LEFT JOIN ResidentTypes rt ON r.resident_type_id = rt.resident_type_id
            WHERE r.is_active = 1
            ORDER BY r.first_name ASC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Resident r = new Resident();
                r.setResidentId(rs.getInt("resident_id"));

                // user_id null gelebilir, kontrol ediyoruz
                int uId = rs.getInt("user_id");
                if (!rs.wasNull()) {
                    r.setUserId(uId);
                }

                r.setResidentTypeId(rs.getInt("resident_type_id"));
                r.setApartmentId(rs.getInt("apartment_id"));
                r.setFirstName(rs.getString("first_name"));
                r.setLastName(rs.getString("last_name"));
                r.setEmail(rs.getString("email"));
                r.setPhoneNumber(rs.getString("phone_number"));

                // Ekstra bilgiler (Görüntüleme için)
                String block = rs.getString("block_name");
                String door = rs.getString("door_number");
                if (block != null && door != null) {
                    r.setFlatInfo(block + " - D:" + door);
                } else {
                    r.setFlatInfo("Daire Atanmamış");
                }

                r.setTypeName(rs.getString("type_name"));

                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Yeni Sakin Ekle (Senin INSERT komutuna uygun)
    public boolean save(Resident resident) {
        // Senin SQL yapın: user_id, resident_type_id, apartment_id, first_name, last_name, email, phone_number
        String sql = """
            INSERT INTO Residents (user_id, resident_type_id, apartment_id, first_name, last_name, email, phone_number, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?, 1)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // 1. user_id (Eğer formdan boş gelirse NULL kaydet)
            if (resident.getUserId() != null && resident.getUserId() > 0) {
                ps.setInt(1, resident.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }

            // 2. resident_type_id (Kiracı/Ev Sahibi)
            ps.setInt(2, resident.getResidentTypeId());

            // 3. apartment_id
            ps.setInt(3, resident.getApartmentId());

            // 4. first_name
            ps.setString(4, resident.getFirstName());

            // 5. last_name
            ps.setString(5, resident.getLastName());

            // 6. email
            ps.setString(6, resident.getEmail());

            // 7. phone_number
            ps.setString(7, resident.getPhoneNumber());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Sakin Sil (Pasife Çek)
    public void delete(int id) {
        String sql = "UPDATE Residents SET is_active = 0 WHERE resident_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}