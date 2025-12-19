package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resident {
    private int residentId;
    private Integer userId;
    private int residentTypeId;
    private int apartmentId;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private boolean isActive = true;

    private String flatInfo;
    private String typeName;
}