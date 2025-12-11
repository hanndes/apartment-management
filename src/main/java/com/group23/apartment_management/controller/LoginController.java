package com.group23.apartment_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    // 1. Mavi Ekran (Sakin Girişi)
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 2. Kırmızı Ekran (Admin Giriş)
    @GetMapping("/admin-login")
    public String showAdminLoginPage() {
        return "admin-login";
    }

    // ---------------------------------------------------------
    // SENARYO A: MAVİ EKRANDAN GİRİŞ (Sakinler İçin)
    // ---------------------------------------------------------
    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {

        User user = userService.authenticate(email, password);

        if (user != null) {
            // KONTROL: Eğer giren kişi ADMIN ise Mavi Ekranda HATA ver!
            if ("ADMIN".equals(user.getRole())) {
                model.addAttribute("error", "Yöneticiler bu ekrandan giriş yapamaz! Lütfen Yönetici Panelini kullanın.");
                return "login"; // Tekrar mavi ekrana döner
            }

            // Eğer Sakin ise (RESIDENT), içeri al
            if ("RESIDENT".equals(user.getRole())) {
                session.setAttribute("loggedInUser", user);
                return "redirect:/user/dashboard";
            }
        }

        // Kullanıcı yoksa veya şifre yanlışsa
        model.addAttribute("error", "Email veya şifre hatalı!");
        return "login";
    }

    // ---------------------------------------------------------
    // SENARYO B: KIRMIZI EKRANDAN GİRİŞ (Sadece Admin İçin)
    // ---------------------------------------------------------
    @PostMapping("/login/admin-auth")
    public String handleAdminLogin(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpSession session,
                                   Model model) {

        User user = userService.authenticate(email, password);

        if (user != null) {
            if ("ADMIN".equals(user.getRole())) {
                session.setAttribute("loggedInUser", user);
                return "redirect:/admin/dashboard";
            } else {
                model.addAttribute("error", "Bu panel sadece yöneticiler içindir!");
                return "admin-login";
            }
        } else {
            model.addAttribute("error", "Email veya şifre hatalı!");
            return "admin-login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}