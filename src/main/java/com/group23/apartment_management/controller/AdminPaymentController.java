package com.group23.apartment_management.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group23.apartment_management.entities.Payment;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.PaymentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPaymentController {
    private final PaymentService paymentService;

    @GetMapping("/payments")
    public String showAllPayments(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);

        // şimdilik dashboard’takiyle aynı metodu kullanıyoruz:
        List<Payment> payments = paymentService.getRecentPaymentsForDashboard(50);
        model.addAttribute("payments", payments);

        model.addAttribute("currentPage", "payments");
        return "admin-payments";  // templates/admin-payments.html
    }
    @GetMapping("/debts")
    public String showDebts(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);

        // Tüm ödemeleri getir
        List<Payment> payments = paymentService.getRecentPaymentsForDashboard(100);
        model.addAttribute("payments", payments);

        model.addAttribute("currentPage", "debts");
        return "admin-payments";
    }
    
}
