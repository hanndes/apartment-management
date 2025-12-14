package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Block;
import org.springframework.stereotype.Repository;
import java.sql.*;

@Repository
public class BlockRepository {

    public Block findById(int blockId) {
        String sql = "SELECT * FROM Blocks WHERE block_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, blockId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Block(
                            rs.getInt("block_id"),
                            rs.getString("block_name"),
                            rs.getInt("total_floors"),
                            rs.getInt("total_apartments"), // Hesaplamada bunu kullanacağız
                            rs.getString("address")
                    );
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}