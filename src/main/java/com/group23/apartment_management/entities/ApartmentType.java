package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentType {
    // Veritabanındaki 'type_id'
    private int id;

    // Veritabanındaki 'type_name' (Örn: 1+1, 2+1)
    private String typeName;
}