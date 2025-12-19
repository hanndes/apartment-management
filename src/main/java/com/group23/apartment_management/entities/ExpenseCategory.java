package com.group23.apartment_management.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseCategory {
    private int id;
    private String name;
}