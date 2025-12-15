package com.group23.apartment_management.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    private int id;
    private String title;
    private String content;
    private String priority;
    private Timestamp createdAt;
    private boolean active;

    public String getFormattedDate() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("dd MMM HH:mm").format(createdAt);
    }

    public String getBadgeClass() {
        if (priority == null) return "info";
        switch (priority) {
            case "Acil": return "danger";   // Kırmızı
            case "Yüksek": return "warning"; // Turuncu
            case "Normal": return "success"; // Yeşil
            default: return "info";          // Mavi
        }
    }
}