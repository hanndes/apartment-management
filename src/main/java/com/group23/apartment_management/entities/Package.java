package com.group23.apartment_management.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Package {
    private int id;
    private int apartmentId;

    private String recipientName;

    private String companyName;
    private Timestamp arrivalDate;
    private boolean delivered;
    private Timestamp deliveryDate;

    public String getFormattedArrivalDate() {
        if (arrivalDate == null) return "";
        return new SimpleDateFormat("dd MMM HH:mm").format(arrivalDate);
    }

    public String getStatusText() {
        return delivered ? "Teslim Edildi" : "Bekliyor";
    }

    public String getStatusClass() {
        return delivered ? "success" : "warning";
    }
}