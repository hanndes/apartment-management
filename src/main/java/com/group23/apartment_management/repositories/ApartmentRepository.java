package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.dto.ApartmentDropdownDTO;
import com.group23.apartment_management.entities.dto.ApartmentDuesDTO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ApartmentRepository {

    // --- 1. METOT: TAHAKKUK (DUES SERVICE) İÇİN GEREKLİ BİLGİLER ---
    // Sadece ID ve Tip ID döndürür. DuesService bunu çağırır.
    public List<ApartmentDuesDTO> findApartmentsForDuesByBlockId(int blockId) {
        List<ApartmentDuesDTO> list = new ArrayList<>();

        String sql = "SELECT apartment_id, type_id FROM Apartments WHERE block_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, blockId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApartmentDuesDTO dto = new ApartmentDuesDTO(
                            rs.getInt("apartment_id"),
                            rs.getInt("type_id")
                    );
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ApartmentDropdownDTO> findApartmentsForDropdownByBlockId(int blockId) {
        List<ApartmentDropdownDTO> list = new ArrayList<>();

        String sql = "SELECT apartment_id, door_number FROM Apartments WHERE block_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, blockId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApartmentDropdownDTO dto = new ApartmentDropdownDTO(
                            rs.getInt("apartment_id"),
                            rs.getString("door_number")
                    );
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}