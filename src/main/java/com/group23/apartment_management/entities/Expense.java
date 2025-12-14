package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    private int id;
    private int categoryId;
    private BigDecimal amount;
    private Date date;
    private String description;

    // YENİ EKLENEN: Blok ID (Hangi binaya harcandı?)
    private Integer blockId;

    // Ekranda Göstermek İçin
    private String categoryName;
    private String blockName; // "A Blok" yazabilmek için
}