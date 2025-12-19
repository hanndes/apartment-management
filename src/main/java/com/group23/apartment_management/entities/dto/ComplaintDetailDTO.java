package com.group23.apartment_management.entities.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ComplaintDetailDTO {
    private int id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private Timestamp createdAt;
    //
    private String response;
    private String userName;
    private String flatInfo;
    private String userPhone;
}