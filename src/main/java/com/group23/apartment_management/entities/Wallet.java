package com.group23.apartment_management.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    private int walletId;
    private int residentId;
    private BigDecimal balance;
    private Timestamp lastUpdated;
}