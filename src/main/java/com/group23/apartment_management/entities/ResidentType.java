package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentType {
    private int id;          // Veritabanındaki resident_type_id
    private String typeName; // Veritabanındaki type_name (Ev Sahibi / Kiracı)
}