package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Debt {
    
    private int id;
    private int apartmentId;
    private int periodId;
    private int debtTypeId;
    private BigDecimal amount;

    private BigDecimal remainingAmount;
    private boolean isPaid;

    private Timestamp createdAt;

    
    private String apartmentInfo; // "A Blok - D:1"
    private String periodName;    // "Ocak 2025"
    private String typeName;      // "Aidat"
}