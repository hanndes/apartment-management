package com.group23.apartment_management.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentSimpleDTO {

    private int id;            // apartment_id
    private String doorNumber; // Kapı No (String olması "1A", "B2" gibi durumlar için daha güvenlidir)

    // Eğer veritabanında door_number kesinlikle sayı ise (int) yapabilirsin:
    // private int doorNumber;
}