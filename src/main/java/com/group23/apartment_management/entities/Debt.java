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
    
    private int id;              // debt_id
    private int apartmentId;     // apartment_id
    private int periodId;        // period_id
    private int debtTypeId;      // debt_type_id
    private BigDecimal amount;   // amount (Asıl Tutar)

    private BigDecimal remainingAmount; // remaining_amt (Kalan Tutar)
    private boolean isPaid;             // is_paid (Tamamen ödendi mi?)

    private Timestamp createdAt; // created_at

    
    private String apartmentInfo; // "A Blok - D:1"
    private String periodName;    // "Ocak 2025"
    private String typeName;      // "Aidat"
}