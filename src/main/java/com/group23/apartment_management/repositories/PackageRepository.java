package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Package;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PackageRepository {

    public List<Package> findPackagesByUserId(int userId) {
        List<Package> list = new ArrayList<>();

        String sql = "SELECT P.* " +
                "FROM Packages P " +
                "JOIN Residents R ON P.apartment_id = R.apartment_id " +
                "WHERE R.user_id = ? " +
                "ORDER BY P.arrival_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Package pkg = new Package();
                    pkg.setId(rs.getInt("package_id"));
                    pkg.setApartmentId(rs.getInt("apartment_id"));

                    pkg.setRecipientName(rs.getString("recipient_name"));

                    pkg.setCompanyName(rs.getString("company_name"));
                    pkg.setArrivalDate(rs.getTimestamp("arrival_date"));
                    pkg.setDelivered(rs.getBoolean("is_delivered"));
                    pkg.setDeliveryDate(rs.getTimestamp("delivery_date"));

                    list.add(pkg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // admin için tüm kargoları detaylı getirir
    public List<Package> findAllWithDetails() {
        List<Package> list = new ArrayList<>();
        String sql = "SELECT p.*, b.block_name, a.door_number, r.first_name, r.last_name " +
                    "FROM Packages p " +
                    "JOIN Apartments a ON p.apartment_id = a.apartment_id " +
                    "LEFT JOIN Blocks b ON a.block_id = b.block_id " +
                    "LEFT JOIN Residents r ON r.apartment_id = a.apartment_id " +
                    "ORDER BY p.arrival_date DESC";
        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Package pkg = new Package();
                pkg.setId(rs.getInt("package_id"));
                pkg.setApartmentId(rs.getInt("apartment_id"));
                pkg.setRecipientName(rs.getString("recipient_name"));
                pkg.setCompanyName(rs.getString("company_name"));
                pkg.setArrivalDate(rs.getTimestamp("arrival_date"));
                pkg.setDelivered(rs.getBoolean("is_delivered"));
                pkg.setDeliveryDate(rs.getTimestamp("delivery_date"));

                String residentName = (rs.getString("first_name") != null) ?
                        rs.getString("first_name") + " " + rs.getString("last_name") : "Boş Daire";
                String aptInfo = (rs.getString("block_name") != null ? rs.getString("block_name") : "-")
                        + " - D:" + rs.getString("door_number") + " (" + residentName + ")";

                pkg.setApartmentInfo(aptInfo);

                list.add(pkg);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void updateDeliveryStatus(int packageId, boolean delivered) {
        String sql = "UPDATE Packages SET is_delivered = ?, delivery_date = ? WHERE package_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, delivered);
            if (delivered) {
                ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(2, java.sql.Types.TIMESTAMP);
            }
            ps.setInt(3, packageId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}