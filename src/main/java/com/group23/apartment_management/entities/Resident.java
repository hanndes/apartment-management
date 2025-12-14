package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resident {
    private int residentId;       // PK
    private Integer userId;       // Users tablosuyla bağlantı 
    private int residentTypeId;   // 1: Ev Sahibi, 2: Kiracı
    private int apartmentId;      // Hangi daire?

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private boolean isActive = true; // Varsayılan olarak aktif

    //Ekranda göstermek için yardımcı alanlar
    private String flatInfo;    // "A Blok - D:1" gibi göstermek için
    private String typeName;    // "Kiracı" yazısı için
}