package com.group23.apartment_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.AnnouncementService;
import com.group23.apartment_management.services.ComplaintService;
import com.group23.apartment_management.services.DebtService;
import com.group23.apartment_management.services.PaymentService;
import com.group23.apartment_management.services.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class AdminDashboardController {
    private final DebtService debtService;
    private final PaymentService paymentService;
    private final AnnouncementService announcementService;
    private final ComplaintService complaintService;
    private final UserService userService;

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session, Model model) {
        // 0) Giriş kontrolü
        User admin = (User) session.getAttribute("loggedInUser");
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", admin);   // sağ üstteki profil/bildirim için

        return "admin-dashboard";
    }
    
}
