package com.group23.apartment_management.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    private int trxId;
    private int walletId;
    private BigDecimal amount;
    private String trxType;    // "DEPOSIT", "PAYMENT"
    private String description;
    private Timestamp trxDate;
}