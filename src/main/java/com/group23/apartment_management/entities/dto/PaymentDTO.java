package com.group23.apartment_management.entities.dto;

import com.group23.apartment_management.entities.Payment;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PaymentDTO extends Payment {

    // Sadece ekranda göstermek için gereken ekstra alanlar
    private String flatNumber; // "A Blok - D:1"
    private String userName;   // "Ahmet Yılmaz"

    // HTML sayfasında 'payment.amount' dendiğinde hata vermemesi için yardımcı metod
    public BigDecimal getAmount() {
        return super.getAmountPaid();
    }
}