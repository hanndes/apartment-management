package com.group23.apartment_management.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentSimpleDTO {

    private int id;
    private String doorNumber;

}