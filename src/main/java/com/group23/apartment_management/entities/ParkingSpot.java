package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpot {
    private int id;
    private int blockId;
    private String spotCode;
    private boolean isOccupied;

    // Ekranda blok ismini göstermek için 
    private String blockName;
}