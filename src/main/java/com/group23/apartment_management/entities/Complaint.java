package com.group23.apartment_management.entities;

import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Data
public class Complaint {
    private int id;
    private int userId;         // SQL: user_id
    private String title;
    private String description;
    private String category;
    private String status;      // Bekliyor, Çözüldü
    private String priority;    // Acil, Normal
    private String adminResponse;
    private Timestamp createdAt;
    private Timestamp resolvedAt;

    public String getFormattedDate() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("dd MMM yyyy").format(createdAt);
    }

    // HTML'de rengi için (Çözüldü -> yeşil, Bekliyor -> sarı)
    public String getStatusClass() {
        if ("Çözüldü".equals(status)) return "success";
        if ("İnceleniyor".equals(status)) return "info";
        if ("Reddedildi".equals(status)) return "danger";
        return "warning";
    }
    // Öncelik Rengi
    public String getPriorityClass() {
        if ("Acil".equals(priority)) return "danger";
        if ("Yüksek".equals(priority)) return "warning";
        return "secondary";
    }
}
