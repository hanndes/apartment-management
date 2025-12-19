package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apartment {
    private int apartmentId;
    private int blockId;
    private int typeId;

    private int floorNumber;
    private String doorNumber;

    private boolean isOccupied;
}