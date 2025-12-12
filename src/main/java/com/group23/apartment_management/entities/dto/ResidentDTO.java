package com.group23.apartment_management.entities.dto;

import lombok.Data;

@Data
public class ResidentDTO {
    // Resident Entity'sinden gelen alanlar
    private int residentId;
    private int residentTypeId;
    private int apartmentId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    // Ekstra Gösterim Alanları
    private String typeName;   // Örn: "Ev Sahibi"
    private String flatInfo;   // Örn: "A Blok - D:5"

    // İsim soyisim birleştirme yardımcısı (HTML'de kullanmak için)
    public String getFullName() {
        return firstName + " " + lastName;
    }
}