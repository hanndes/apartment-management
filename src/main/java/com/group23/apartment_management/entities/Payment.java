package com.group23.apartment_management.entities;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private int id;
    private int debtId;
    private Integer residentId;

    private BigDecimal amountPaid;    // amount_paid
    private Timestamp paymentDate;  // payment_date
    private String paymentMethod;     // payment_method
    private String referenceNo;       // reference_no
    private Timestamp createdAt;      // created_at

    public String getFormattedPaymentDate() {
        if (paymentDate == null) return "";
        return new SimpleDateFormat("dd MMM yyyy HH:mm").format(paymentDate);
    }

    public String getMethodBadgeClass() {
        if (paymentMethod == null) return "secondary";
        switch (paymentMethod) {
            case "CASH": return "info";
            case "CARD": return "success";
            case "BANK": return "primary";
            default: return "secondary";
        }
    }

}
