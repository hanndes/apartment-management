package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Block {

    private int blockId;            // Veritabanı: block_id
    private String blockName;       // Veritabanı: block_name

    private int totalFloors;        // Veritabanı: total_floors
    private int totalApartments;    // Veritabanı: total_apartments
    private String address;         // Veritabanı: address
}