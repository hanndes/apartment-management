package com.group23.apartment_management.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    // Veritabanından gelen temel bilgiler
    private int id;
    private BigDecimal amount;      // DB'deki amount_paid buraya eşleşecek
    private Timestamp paymentDate;
    private String paymentMethod;

    // Ekranda göstermek için ekstra alanlar
    private String flatNumber;      // "A Blok - D:1"
    private String userName;        // "Ahmet Yılmaz"
}