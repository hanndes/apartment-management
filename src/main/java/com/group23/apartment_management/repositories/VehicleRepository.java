package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Vehicle;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VehicleRepository {

    // Tüm araçları getir (Sahiplerinin isimleriyle birlikte JOIN yaparak)
    public List<Vehicle> findAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = """
            SELECT v.*, r.first_name, r.last_name, b.block_name, a.door_number
            FROM Vehicles v
            JOIN Residents r ON v.resident_id = r.resident_id
            LEFT JOIN Apartments a ON r.apartment_id = a.apartment_id
            LEFT JOIN Blocks b ON a.block_id = b.block_id
            ORDER BY v.is_active DESC, v.plate_number ASC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleId(rs.getInt("vehicle_id"));
                v.setResidentId(rs.getInt("resident_id"));
                v.setPlateNumber(rs.getString("plate_number"));
                v.setBrand(rs.getString("brand"));
                v.setModel(rs.getString("model"));
                v.setColor(rs.getString("color"));
                v.setVehicleType(rs.getString("vehicle_type"));
                v.setActive(rs.getBoolean("is_active"));

                // Ekstra Bilgiler
                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
                v.setOwnerName(fullName);

                String flat = rs.getString("block_name") + " - D:" + rs.getString("door_number");
                v.setFlatInfo(flat);

                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Yeni Araç Ekle
    public boolean save(Vehicle vehicle) {
        String sql = "INSERT INTO Vehicles (resident_id, plate_number, brand, model, color, vehicle_type, is_active) VALUES (?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, vehicle.getResidentId());
            ps.setString(2, vehicle.getPlateNumber().toUpperCase());
            ps.setString(3, vehicle.getBrand());
            ps.setString(4, vehicle.getModel());
            ps.setString(5, vehicle.getColor());
            ps.setString(6, vehicle.getVehicleType());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Aracı Sil (Pasife Çek)
    public void delete(int vehicleId) {
        String sql = "UPDATE Vehicles SET is_active = 0 WHERE vehicle_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    



    public List<Vehicle> findVehiclesByUserId(int userId) {
        List<Vehicle> list = new ArrayList<>();

        // JOIN işlemi: Araç -> Sakin -> Kullanıcı
        String sql = "SELECT v.* " +
                "FROM Vehicles v " +
                "JOIN Residents r ON v.resident_id = r.resident_id " +
                "WHERE r.user_id = ? AND v.is_active = 1";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId); 

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehicle v = new Vehicle();
                    v.setVehicleId(rs.getInt("vehicle_id"));
                    v.setResidentId(rs.getInt("resident_id"));
                    v.setPlateNumber(rs.getString("plate_number"));
                    v.setBrand(rs.getString("brand"));
                    v.setModel(rs.getString("model"));
                    v.setColor(rs.getString("color"));
                    v.setVehicleType(rs.getString("vehicle_type"));
                    v.setActive(rs.getBoolean("is_active"));

                    list.add(v);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}