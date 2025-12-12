package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuesPeriod {
    private int id;            // period_id
    private String periodName; // period_name (Örn: Ocak 2025)
    private int year;
    private int month;
    private Date dueDate;      // due_date
    private boolean isClosed;  // is_closed
}