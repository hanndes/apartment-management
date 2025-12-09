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
}