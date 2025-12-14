package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private int vehicleId;
    private int residentId;

    private String plateNumber;
    private String brand;
    private String model;
    private String color;
    private String vehicleType;
    private boolean active;

    // --- Ekranda Göstermek İçin Ekstra Alanlar (Veritabanında yok) ---
    private String ownerName; // Sahibinin adı (Ahmet Yılmaz)
    private String flatInfo;  // Daire bilgisi (A Blok No:5)

    // HTML'de rozet rengi için
    public String getStatusClass() {
        return active ? "success" : "danger";
    }

    public String getStatusText() {
        return active ? "Aktif" : "Pasif";
    }

    public String getFullName() {
        return brand + " " + model;
    }

    public String getFormattedPlate() {
        if (plateNumber == null) return "";
        return plateNumber.toUpperCase(); 
    }
    
}