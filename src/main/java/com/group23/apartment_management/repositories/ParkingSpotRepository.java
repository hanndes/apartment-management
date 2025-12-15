package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.ParkingSpot;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ParkingSpotRepository {

    public List<ParkingSpot> findAll() {
        List<ParkingSpot> list = new ArrayList<>();
        String sql = "SELECT p.*, b.block_name " +
                "FROM ParkingSpots p " +
                "JOIN Blocks b ON p.block_id = b.block_id " +
                "ORDER BY b.block_name, p.spot_code";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ParkingSpot p = new ParkingSpot();
                p.setId(rs.getInt("parking_spot_id"));
                p.setBlockId(rs.getInt("block_id"));
                p.setSpotCode(rs.getString("spot_code"));
                // is_guest sütununu okuyoruz
                boolean isGuest = rs.getBoolean("is_guest");

// DİKKAT: is_guest true ise, spot.occupied de true (dolu) olsun.
                p.setOccupied(isGuest);
                p.setOccupied(rs.getBoolean("is_guest"));
                p.setBlockName(rs.getString("block_name"));
                list.add(p);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean save(ParkingSpot spot) {
        String sql = """
            INSERT INTO ParkingSpots (block_id, spot_code, is_guest, is_occupied)
            VALUES (?, ?, 0, 0)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, spot.getBlockId());
            ps.setString(2, spot.getSpotCode());

            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            System.out.println("PARKING INSERT ERROR: " + e.getMessage());
            e.printStackTrace(); 
            return false; }
    }

    public void delete(int id) {
        String sql = "DELETE FROM ParkingSpots WHERE parking_spot_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}