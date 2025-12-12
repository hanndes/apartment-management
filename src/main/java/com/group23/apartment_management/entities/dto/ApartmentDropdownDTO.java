package com.group23.apartment_management.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApartmentDropdownDTO {
    private int id;
    private String label; // "A Blok - D:1" gibi görünecek
}