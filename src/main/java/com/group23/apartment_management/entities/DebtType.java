package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebtType {
    private int id;           // debt_type_id
    private String typeCode;  // type_code (AIDAT)
    private String typeName;  // type_name (Aylık Aidat)
}