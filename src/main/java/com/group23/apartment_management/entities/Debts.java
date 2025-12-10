package com.group23.apartment_management.entities;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Debts {
    private int id;
    private int apartmentId;
    private int periodId;
    private int debtTypeId;

    private BigDecimal amount;
    private BigDecimal remainingAmt;
    private boolean paid;

    private Timestamp createdAt;

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return new SimpleDateFormat("dd MMM HH:mm").format(createdAt);
    }

    public String getStatusText() {
        return paid ? "Ödendi" : "Ödenmedi";
    }

    public String getStatusClass() {
        return paid ? "success" : "warning"; // badge rengi için
    }
    
}
