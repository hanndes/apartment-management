package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    private int staffId;
    private String firstName;
    private String lastName;
    private String role;        // Görevi
    private String phoneNumber;
    private BigDecimal salary;  // Maaş
    private Date startDate;     // İşe Giriş
    private boolean active;     // Aktif mi?

    public String getFullName() {
        return firstName + " " + lastName;
    }
}