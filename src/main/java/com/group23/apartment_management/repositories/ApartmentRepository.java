package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.dto.ApartmentDropdownDTO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ApartmentRepository {

    
   
     // Bu metot, Gider Dağıtımı (Expense Distribution) sırasında kullanılır.   
    public List<ApartmentDropdownDTO> findByBlockId(int blockId) {
        List<ApartmentDropdownDTO> list = new ArrayList<>();

        
        String sql = "SELECT apartment_id, door_number FROM Apartments WHERE block_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, blockId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ApartmentDropdownDTO dto = new ApartmentDropdownDTO(rs.getInt("apartment_id"),rs.getString("door_number"));


                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    
    public List<ApartmentDropdownDTO> findAll() {
        List<ApartmentDropdownDTO> list = new ArrayList<>();
        String sql = "SELECT apartment_id, door_number FROM Apartments"; 

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ApartmentDropdownDTO dto = new ApartmentDropdownDTO(rs.getInt("apartment_id"),rs.getString("door_number"));


                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}