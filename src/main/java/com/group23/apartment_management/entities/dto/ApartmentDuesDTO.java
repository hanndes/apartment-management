package com.group23.apartment_management.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDuesDTO {

    // Tahakkuk işlemi için gerekli minimum bilgiler
    private int apartmentId;   // Daire ID (Borç kime yazılacak?)
    private int typeId;        // Daire Tipi ID'si (Borç tutarı ne olmalı?)
}