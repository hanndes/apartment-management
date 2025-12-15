package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Block;
import com.group23.apartment_management.entities.Resident;
import com.group23.apartment_management.entities.ResidentType; 
import com.group23.apartment_management.entities.dto.ApartmentDropdownDTO;
import com.group23.apartment_management.entities.dto.ResidentDTO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ResidentRepository {

    public Integer findResidentIdByUserId(int userId) {
        String sql = "SELECT resident_id FROM Residents WHERE user_id = ?";
        Integer resId = null;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resId = rs.getInt("resident_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resId;
    }
    // 1. LİSTELEME: Sakinleri Detaylı Getir
    public List<ResidentDTO> findAllResidentsWithDetails() {
        List<ResidentDTO> list = new ArrayList<>();

        String sql = "SELECT r.*, rt.type_name, b.block_name, a.door_number " +
                "FROM Residents r " +
                "LEFT JOIN ResidentTypes rt ON r.resident_type_id = rt.resident_type_id " +
                "LEFT JOIN Apartments a ON r.apartment_id = a.apartment_id " +
                "LEFT JOIN Blocks b ON a.block_id = b.block_id " +
                "ORDER BY r.first_name ASC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ResidentDTO dto = new ResidentDTO();
                dto.setResidentId(rs.getInt("resident_id"));
                dto.setFirstName(rs.getString("first_name"));
                dto.setLastName(rs.getString("last_name"));
                dto.setPhoneNumber(rs.getString("phone_number"));
                dto.setEmail(rs.getString("email"));
                dto.setResidentTypeId(rs.getInt("resident_type_id"));
                dto.setApartmentId(rs.getInt("apartment_id"));

                // Ekstra Bilgiler
                dto.setTypeName(rs.getString("type_name"));

                String block = rs.getString("block_name");
                String door = rs.getString("door_number");
                if(block != null) {
                    dto.setFlatInfo(block + " - D:" + door);
                } else {
                    dto.setFlatInfo("-");
                }

                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
// ResidentRepository.java

    // 2. KAYDETME (Güncellenmiş: Yeni ID'yi döndürür ve UNIQUE KEY hatasını önler)
    public int save(Resident resident) {

        // Güvenli E-posta Değeri: NULL yerine boş string ("") gönderilerek
        // UNIQUE kısıtlamasının NULL değerlerde çakışması önlenir.
        String emailValue = resident.getEmail();
        if (emailValue == null || emailValue.trim().isEmpty()) {
            emailValue = "";
        }

        // SQL Sorgusu: Tüm alanları dahil ediyoruz, E-postayı yukarıda kontrol ettik.
        // DİKKAT: user_id bu sorguda yoktur, tabloda NULL'a izin verildiği varsayılır.
        String sql = "INSERT INTO Residents (resident_type_id, apartment_id, first_name, last_name, phone_number, email) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        // Yeni oluşturulan anahtarı (ID) geri almak için Statement.RETURN_GENERATED_KEYS kullanılır.
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, resident.getResidentTypeId());
            ps.setInt(2, resident.getApartmentId());
            ps.setString(3, resident.getFirstName());
            ps.setString(4, resident.getLastName());
            ps.setString(5, resident.getPhoneNumber());
            ps.setString(6, emailValue); // Kontrol edilmiş e-posta değerini gönder

            ps.executeUpdate();

            // Otomatik oluşturulan resident_id'yi geri al
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Yeni oluşturulan ID'yi döndür
                    return generatedKeys.getInt(1);
                }
            }
        } catch (Exception e) {
            // Hata durumunda konsola yazdır ve -1 döndür.
            e.printStackTrace();
        }
        return -1; // Kayıt başarısız olursa -1 döndür
    }
    // 3. SİLME
    public void delete(int id) {
        String sql = "DELETE FROM Residents WHERE resident_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4. DROPDOWN: Sakin Tiplerini Getir
    public List<ResidentType> findAllTypes() {
        List<ResidentType> list = new ArrayList<>();
        String sql = "SELECT * FROM ResidentTypes";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()){
                ResidentType rt = new ResidentType();
                rt.setId(rs.getInt("resident_type_id"));
                rt.setTypeName(rs.getString("type_name"));
                list.add(rt);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 5. DROPDOWN: Daireleri Getir
    public List<ApartmentDropdownDTO> findAllApartmentsForDropdown() {
        List<ApartmentDropdownDTO> list = new ArrayList<>();
        String sql = "SELECT a.apartment_id, b.block_name, a.door_number " +
                "FROM Apartments a " +
                "JOIN Blocks b ON a.block_id = b.block_id ORDER BY b.block_name, a.door_number";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()){
                String label = rs.getString("block_name") + " Blok - D:" + rs.getString("door_number");
                list.add(new ApartmentDropdownDTO(rs.getInt("apartment_id"), label));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    // Blokları Listele 
    public List<Block> findAllBlocks() {
        List<Block> list = new ArrayList<>();
        
        String sql = "SELECT * FROM Blocks ORDER BY block_name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Block b = new Block();
                b.setBlockId(rs.getInt("block_id"));
                b.setBlockName(rs.getString("block_name"));

               
                b.setTotalFloors(rs.getInt("total_floors"));
                b.setTotalApartments(rs.getInt("total_apartments"));
                b.setAddress(rs.getString("address"));

                list.add(b);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    

    public Integer findApartmentIdByResidentId(int residentId) {
        String sql = "SELECT apartment_id FROM Residents WHERE resident_id = ?";
        Integer aptId = null;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, residentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    aptId = rs.getInt("apartment_id");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return aptId;
    }


}