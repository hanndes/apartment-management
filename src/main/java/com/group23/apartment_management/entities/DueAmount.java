package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp; // created_at DATETIME için gerekli olabilir

@Data // Getter, Setter, toString, equals, hashCode sağlar
@NoArgsConstructor // No-argument constructor sağlar
@AllArgsConstructor // Tüm argümanlarla constructor sağlar
public class DueAmount {

    // Veritabanı Sütunları

    private Integer dueId;             // PK: due_id

    private Integer apartmentTypeId;   // FK: apartment_type_id (Örn: 1, 2, 3)
    private Integer periodId;          // FK: period_id (Örn: Ocak 2026)
    private Integer debtTypeId;        // FK: debt_type_id (Örn: 1 -> AIDAT)

    private BigDecimal amount;         // Tutar (Örn: 2500.00)

    // Opsiyonel olarak, eğer DueAmounts tablonuzda varsa, bu alanları da ekleyebilirsiniz:
    // private Timestamp createdAt;

}