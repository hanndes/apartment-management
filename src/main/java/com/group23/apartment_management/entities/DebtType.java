package com.group23.apartment_management.entities;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityScan
public class DebtType {
    private int id;
    private String typeCode;
    private String typeName;
    private String description;
    private boolean active;
    
}
