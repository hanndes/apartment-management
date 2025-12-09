package com.group23.apartment_management.entities;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuesPeriod {
    private int id;
    private String periodName;
    private int year;
    private int month;
    private Timestamp dueDate;
    private boolean closed;

    // Tarihi frontend'de düzgün göstermek için formatlayıcı
    public String getFormattedDueDate() {
        if (dueDate == null) return "";
        return new SimpleDateFormat("dd MMM yyyy").format(dueDate);
    }

    // Dönem durumunu yazı olarak göstermek için (Örn: Tabloda yazdırmak için)
    public String getStatusText() {
        return closed ? "Kapandı" : "Aktif";
    }

    // Frontend'de (HTML/Thymeleaf) badge rengini ayarlamak için
    // Kapandı -> Kırmızı (danger) veya Gri (secondary), Aktif -> Yeşil (success)
    public String getStatusClass() {
        return closed ? "secondary" : "success";
    }
}
