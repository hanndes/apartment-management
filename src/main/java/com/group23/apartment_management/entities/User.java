package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;          
    private String username;
    private String password;
    private String email;
    private String phoneNumber; 
    private String role; 
    private boolean active;
}