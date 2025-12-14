package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Complaint;
import com.group23.apartment_management.entities.dto.ComplaintDTO;
import com.group23.apartment_management.entities.dto.ComplaintDetailDTO;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ComplaintRepository {

    public boolean save(Complaint complaint) {
        String sql = "INSERT INTO Complaints (user_id, title, description, category, priority, status, created_at) VALUES (?, ?, ?, ?, ?, 'Bekliyor', GETDATE())";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, complaint.getUserId());
            ps.setString(2, complaint.getTitle());
            ps.setString(3, complaint.getDescription());
            ps.setString(4, complaint.getCategory());
            ps.setString(5, complaint.getPriority());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
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
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(c);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Admin Listeleme (DTO)
    public List<ComplaintDTO> findAllComplaintsWithNames() {
        List<ComplaintDTO> list = new ArrayList<>();

        String sql = "SELECT c.*, r.first_name, r.last_name " +
                "FROM Complaints c " +
                "LEFT JOIN Residents r ON c.user_id = r.user_id " +
                "ORDER BY c.created_at DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ComplaintDTO dto = new ComplaintDTO();
                dto.setId(rs.getInt("complaint_id"));
                dto.setTitle(rs.getString("title"));
                dto.setDescription(rs.getString("description"));
                dto.setCategory(rs.getString("category"));
                dto.setStatus(rs.getString("status"));
                dto.setPriority(rs.getString("priority"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));

                String fname = rs.getString("first_name");
                String lname = rs.getString("last_name");
                if (fname != null) {
                    dto.setUserName(fname + " " + lname);
                } else {
                    dto.setUserName("Misafir");
                }
                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // DETAY SAYFASI İÇİN ÖZEL METOD (ComplaintDetailDTO DÖNDÜRÜR)
    public ComplaintDetailDTO findComplaintDetailById(int id) {
        ComplaintDetailDTO dto = null;

        String sql = "SELECT c.*, r.first_name, r.last_name, r.phone_number, " +
                "a.door_number, b.block_name " +
                "FROM Complaints c " +
                "LEFT JOIN Residents r ON c.user_id = r.user_id " +
                "LEFT JOIN Apartments a ON r.apartment_id = a.apartment_id " +
                "LEFT JOIN Blocks b ON a.block_id = b.block_id " +
                "WHERE c.complaint_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new ComplaintDetailDTO();

                    dto.setId(rs.getInt("complaint_id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setDescription(rs.getString("description"));
                    dto.setCategory(rs.getString("category"));
                    dto.setPriority(rs.getString("priority"));
                    dto.setStatus(rs.getString("status"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));

                    // --- DÜZELTME BURADA YAPILDI ---
                    // Veritabanındaki kolon adı 'admin_response'
                    dto.setResponse(rs.getString("admin_response"));

                    String fname = rs.getString("first_name");
                    String lname = rs.getString("last_name");
                    dto.setUserName(fname != null ? fname + " " + lname : "Misafir");

                    String block = rs.getString("block_name");
                    String door = rs.getString("door_number");
                    if (block != null && door != null) {
                        dto.setFlatInfo(block + " Blok - Daire " + door);
                    } else {
                        dto.setFlatInfo("Daire Bilgisi Yok");
                    }

                    dto.setUserPhone(rs.getString("phone_number"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }
    public void updateResponseAndStatus(int id, String response, String status) {
        String sql = "UPDATE Complaints SET admin_response = ?, status = ? WHERE complaint_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, response);
            ps.setString(2, status);
            ps.setInt(3, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

            public int countByStatus(String status) {
                int count = 0;
                // Aggregation Query: Tüm satırları çekmek yerine sadece sayıyı alıyoruz
                String sql = "SELECT COUNT(*) AS total FROM Complaints WHERE status = ?";

                try (Connection con = DatabaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setString(1, status);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            count = rs.getInt("total");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return count;
            }

}