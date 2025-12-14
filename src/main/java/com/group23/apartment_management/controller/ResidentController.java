package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.Complaint;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.entities.Wallet; // Wallet eklendi
import com.group23.apartment_management.services.*;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal; // BigDecimal eklendi

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Mesajlar için

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class ResidentController {

    private final AnnouncementService announcementService;
    private final PackageService packageService;
    private final ComplaintService complaintService;
    private final PaymentService paymentService;
    private final VehicleService vehicleService;
    private final DebtService debtService;
    private final WalletService walletService; // YENİ: Cüzdan Servisi

    // Güvenlik kontrolü
    private boolean isResident(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "RESIDENT".equals(user.getRole());
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("announcements", announcementService.getUrgentAnnouncements());
        model.addAttribute("packages", packageService.getUserPackages(user.getId()));
        model.addAttribute("complaints", complaintService.getUserComplaints(user.getId()));
        model.addAttribute("vehicles", vehicleService.getVehiclesByUserId(user.getId()));
        model.addAttribute("recentPayments", paymentService.getUserPayments(user.getId()));

        model.addAttribute("currentPage", "dashboard");
        return "user-dashboard";
    }

    // BORÇLAR / ÖDEMELER SAYFASI (GÜNCELLENDİ)
    @GetMapping("/payments")
    public String showMyPayments(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // 1. Borç Listesi
        model.addAttribute("debts", debtService.getUserDebts(user.getId()));

        // 2. YENİ: Cüzdan Bakiyesini Getir ve Modele Ekle
        Wallet wallet = walletService.getUserWallet(user.getId());
        BigDecimal currentBalance = (wallet != null) ? wallet.getBalance() : BigDecimal.ZERO;

        model.addAttribute("walletBalance", currentBalance);

        model.addAttribute("currentPage", "payments");
        return "user-payments";
    }
    @PostMapping("/payments/pay-wallet")
    public String payWithWallet(@RequestParam("debtId") int debtId,
                                HttpSession session,
                                Model model) { // Model ekledik (Veri göndermek için)

        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");

        try {
            // 1. Ödeme İşlemini Yap
            walletService.payDebtWithWallet(user.getId(), debtId);
            model.addAttribute("successMessage", "Ödeme başarıyla alındı.");

        } catch (RuntimeException e) {
            // Hata varsa mesajı modele ekle
            model.addAttribute("errorMessage", e.getMessage());
        }

        // --- YÖNLENDİRME YOK, VERİLERİ TEKRAR YÜKLÜYORUZ ---

        // 2. Güncel Borç Listesini Çek
        model.addAttribute("debts", debtService.getUserDebts(user.getId()));

        // 3. Güncel Bakiyeyi Çek
        Wallet wallet = walletService.getUserWallet(user.getId());
        BigDecimal currentBalance = (wallet != null) ? wallet.getBalance() : BigDecimal.ZERO;
        model.addAttribute("walletBalance", currentBalance);

        model.addAttribute("user", user);
        model.addAttribute("currentPage", "payments");

        // 4. Doğrudan sayfayı döndür (URL değişmez)
        return "user-payments";
    }
    // NORMAL (KARTLA) ÖDEME YAP (Eski metod - durabilir)
    @PostMapping("/payments/pay")
    public String makePayment(@RequestParam int debtId, HttpSession session) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        paymentService.processPayment(debtId, user.getId());

        return "redirect:/user/payments";
    }

    // AİDAT ÖDE SAYFASI
    @GetMapping("/aidat-ode")
    public String aidatOdePage(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("debts", debtService.getUserDebts(user.getId()));

        model.addAttribute("currentPage", "aidat");
        return "user-aidatOde";
    }

    // ŞİKAYETLER
    @GetMapping("/complaints")
    public String showComplaints(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);
        model.addAttribute("complaints", complaintService.getUserComplaints(user.getId()));
        model.addAttribute("newComplaint", new Complaint());

        model.addAttribute("currentPage", "complaints");
        return "user-complaints";
    }

    @PostMapping("/complaints/add")
    public String addComplaint(@ModelAttribute Complaint complaint, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            complaint.setUserId(user.getId());
            complaintService.createComplaint(complaint);
        }
        return "redirect:/user/complaints";
    }

    // DUYURULAR
    @GetMapping("/announcements")
    public String showAnnouncementsPage(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);
        model.addAttribute("announcements", announcementService.getActiveAnnouncements());

        model.addAttribute("currentPage", "announcements");
        return "user-announcements";
    }
}