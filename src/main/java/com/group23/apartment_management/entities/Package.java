package com.group23.apartment_management.entities;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // admin listesinde göstermek için (blok - daire - sakin)
    private String apartmentInfo;

    // HTML'de ${pkg.formattedDeliveryDate} için gerekli
    public String getFormattedDeliveryDate() {
        if (this.deliveryDate == null) {
            return ""; 
        }
        // Tarihi güzel formatlamak için
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM HH:mm");
        return sdf.format(this.deliveryDate);
    }

    // HTML'de ${pkg.formattedArrivalDate} için gerekli (bunu da eklemezsen bir sonraki hatan bu olur)
    public String getFormattedArrivalDate() {
        if (this.arrivalDate == null) {
            return "-";
        }
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM HH:mm");
        return sdf.format(this.arrivalDate);
    }

    // HTML'de ${pkg.statusClass} için gerekli (CSS rengi)
    public String getStatusClass() {
        return delivered ? "success" : "warning";
    }

    // HTML'de ${pkg.statusText} için gerekli (Ekranda yazan metin)
    public String getStatusText() {
        return delivered ? "Teslim Edildi" : "Bekliyor";
    }

    // HTML'de ${pkg.apartmentInfo} için gerekli
    public String getApartmentInfo() {
        // Eğer apartment objesi bağlı değilse sadece ID döndürürüz hata almamak için
        return "Daire ID: " + this.apartmentId;
    }
}