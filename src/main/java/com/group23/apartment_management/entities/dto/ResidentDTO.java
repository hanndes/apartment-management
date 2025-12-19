package com.group23.apartment_management.entities.dto;

import lombok.Data;

@Data
public class ResidentDTO {

    private int residentId;
    private int residentTypeId;
    private int apartmentId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;

    private String typeName;
    private String flatInfo;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}