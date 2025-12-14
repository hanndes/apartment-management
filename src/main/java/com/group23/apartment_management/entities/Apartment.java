package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {
    // Tablo: Apartments

    private int apartmentId;     // apartment_id (PK)
    private int blockId;         // block_id (FK - Blocks tablosuna gider)
    private int typeId;          // type_id (FK - ApartmentTypes tablosuna gider)

    private int floorNumber;     // floor_number
    private String doorNumber;   // door_number 

    private boolean isOccupied;  // is_occupied (Dolu mu boş mu?)
}