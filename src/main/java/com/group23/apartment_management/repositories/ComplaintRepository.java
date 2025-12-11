package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Complaint;
import com.group23.apartment_management.entities.dto.ComplaintDTO; // DTO'yu ekledik
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ComplaintRepository {

    // Ekleme Metodu (Aynı kalıyor)
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

    // User İçin Listeleme (Aynı kalıyor)
    public List<Complaint> findByUserId(int userId) {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT * FROM Complaints WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Complaint c = new Complaint();
                    // ... (setleme işlemleri aynı) ...
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

    // --- ADMIN İÇİN: DTO DÖNDÜREN METOD ---
    // List<Complaint> değil, List<ComplaintDTO> döndürüyoruz
    public List<ComplaintDTO> findAllComplaintsWithNames() {
        List<ComplaintDTO> list = new ArrayList<>();

        String sql = """
            SELECT c.*, r.first_name, r.last_name 
            FROM Complaints c
            LEFT JOIN Residents r ON c.user_id = r.user_id
            ORDER BY c.created_at DESC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Burada DTO nesnesi oluşturuyoruz
                ComplaintDTO dto = new ComplaintDTO();

                // Normal alanları doldur
                dto.setId(rs.getInt("complaint_id"));
                dto.setTitle(rs.getString("title"));
                dto.setDescription(rs.getString("description"));
                dto.setCategory(rs.getString("category"));
                dto.setStatus(rs.getString("status"));
                dto.setPriority(rs.getString("priority"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));

                // İsim alanını doldur (DTO'nun özelliği)
                String fname = rs.getString("first_name");
                String lname = rs.getString("last_name");
                // YENİSİ (Doğru):
                if (fname != null) {
                    dto.setUserName(fname + " " + lname); // DTO'daki "userName" alanını kullanıyoruz
                } else {
                    dto.setUserName("Misafir");
                }

                list.add(dto);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // Durum Güncelleme (Aynı kalıyor)
    public void updateStatus(int id, String status) {
        String sql = "UPDATE Complaints SET status = ? WHERE complaint_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}