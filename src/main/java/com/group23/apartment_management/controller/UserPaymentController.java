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
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserPaymentController {
        private final PaymentService paymentService;

    // "Ödemelerim" sayfası
    @GetMapping("/payments")
    public String showUserPayments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"RESIDENT".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Payment> payments = paymentService.getUserPayments(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("payments", payments);

        return "user-payments"; // templates/user-payments.html
    }
}
