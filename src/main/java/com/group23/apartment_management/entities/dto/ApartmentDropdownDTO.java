package com.group23.apartment_management.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDropdownDTO {

    private int id;            // apartment_id
    private String doorNumber; // door_number
    public String getLabel() {
    return this.doorNumber;
}
}