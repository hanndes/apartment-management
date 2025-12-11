package com.group23.apartment_management.entities.dto;

import lombok.Data;

import java.sql.Timestamp;

// ComplaintDTO.java
@Data // Lombok getter/setter'ları oluşturur
public class ComplaintDTO {
    private int id;
    private String title;
    private String description;
    private String category;
    private String status;
    private String priority;
    private Timestamp createdAt;
    // HATANIN SEBEBİ: HTML 'user' arıyor ama burada yok.
    // ÇÖZÜM: 'user' nesnesi yerine sadece ismini taşıyalım.
    private String userName; // Şikayeti yapan kişinin adı

    // Tarih vb. diğer alanlar...
}