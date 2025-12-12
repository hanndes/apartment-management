package com.group23.apartment_management.entities.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ComplaintDetailDTO {
    private int id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private Timestamp createdAt;

    // Detay sayfası için ekstra alanlar:
    private String response;   // Adminin verdiği cevap (Veritabanına eklediğimiz alan)
    private String userName;   // "Ahmet Yılmaz"
    private String flatInfo;   // "A Blok - D:5" (Arıza nerede bilsin diye)
    private String userPhone;  // Gerekirse telefon numarasını da çekebiliriz
}