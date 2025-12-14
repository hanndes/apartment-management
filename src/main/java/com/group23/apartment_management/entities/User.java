package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;          // HTML'de ${u.id} kullanıldığı için
    private String username;
    private String password;
    private String email;
    private String phoneNumber; // <-- YENİ EKLENEN ALAN
    private String role; //Join ile geliyo
    private boolean active;
}