package com.group23.apartment_management.repositories;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Expense;
import com.group23.apartment_management.entities.ExpenseCategory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExpenseRepository {

    // 1. GİDERLERİ LİSTELE (Kategori Adıyla Beraber)
    public List<Expense> findAll() {
        List<Expense> list = new ArrayList<>();

        String sql = "SELECT e.exp_id, e.cat_id, e.amount, e.exp_date, e.description, c.cat_name " +
                "FROM Expenses e " +
                "LEFT JOIN ExpenseCategories c ON e.cat_id = c.cat_id " +
                "ORDER BY e.exp_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Expense ex = new Expense();
                ex.setId(rs.getInt("exp_id"));
                ex.setCategoryId(rs.getInt("cat_id"));
                ex.setAmount(rs.getBigDecimal("amount"));
                ex.setDate(rs.getDate("exp_date"));
                ex.setDescription(rs.getString("description"));

                // JOIN ile gelen kategori adı
                ex.setCategoryName(rs.getString("cat_name"));

                list.add(ex);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. YENİ GİDER EKLE
    public boolean save(Expense expense) {
        String sql = "INSERT INTO Expenses (cat_id, amount, exp_date, description) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, expense.getCategoryId());
            ps.setBigDecimal(2, expense.getAmount());
            ps.setDate(3, expense.getDate());
            ps.setString(4, expense.getDescription());

            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // 3. GİDER SİL
    public void delete(int id) {
        String sql = "DELETE FROM Expenses WHERE exp_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 4. KATEGORİLERİ GETİR (Dropdown İçin)
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