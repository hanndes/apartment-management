package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Expense;
import com.group23.apartment_management.entities.ExpenseCategory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExpenseRepository {

    public List<Expense> findAll() {
        List<Expense> list = new ArrayList<>();

  
        String sql = "SELECT e.exp_id, e.cat_id, e.block_id, e.amount, e.exp_date, e.description, " +
                "c.cat_name, b.block_name " +
                "FROM Expenses e " +
                "JOIN ExpenseCategories c ON e.cat_id = c.cat_id " +
                "JOIN Blocks b ON e.block_id = b.block_id " +
                "ORDER BY e.exp_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Expense ex = new Expense();
                ex.setId(rs.getInt("exp_id"));
                ex.setCategoryId(rs.getInt("cat_id"));
                ex.setBlockId(rs.getInt("block_id"));
                ex.setAmount(rs.getBigDecimal("amount"));
                ex.setDate(rs.getDate("exp_date"));
                ex.setDescription(rs.getString("description"));

                ex.setCategoryName(rs.getString("cat_name"));
                ex.setBlockName(rs.getString("block_name"));

                list.add(ex);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int save(Expense expense) {
        String sql = "INSERT INTO Expenses (cat_id, block_id, amount, exp_date, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, expense.getCategoryId());
            ps.setInt(2, expense.getBlockId());    
            ps.setBigDecimal(3, expense.getAmount());
            ps.setDate(4, expense.getDate());
            ps.setString(5, expense.getDescription());

            ps.executeUpdate();

           
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0; 
    }

    public void delete(int id) {
        String sql = "DELETE FROM Expenses WHERE exp_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public List<ExpenseCategory> findAllCategories() {
        List<ExpenseCategory> list = new ArrayList<>();
        String sql = "SELECT cat_id, cat_name FROM ExpenseCategories";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ExpenseCategory(rs.getInt("cat_id"), rs.getString("cat_name")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}