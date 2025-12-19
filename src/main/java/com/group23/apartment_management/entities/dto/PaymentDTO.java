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

    private int id;
    private BigDecimal amount;      
    private Timestamp paymentDate;
    private String paymentMethod;

    private String flatNumber;
    private String userName;
}