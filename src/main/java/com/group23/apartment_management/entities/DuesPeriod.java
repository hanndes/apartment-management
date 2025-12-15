package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuesPeriod {
    private int id;
    private String periodName;
    private int year;
    private int month;
    private Date dueDate;
    private boolean isClosed;
}