package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.Payment;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.AnnouncementService;
import com.group23.apartment_management.services.DebtService;
import com.group23.apartment_management.services.PaymentService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin") // Tüm admin linkleri "/admin" ile başlar
@RequiredArgsConstructor
public class AdminController {

    private final DebtService debtService;
    private final PaymentService paymentService;
    private final AnnouncementService announcementService;

    // --- GÜVENLİK KONTROLÜ METODU (Kod tekrarını önler) ---
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    // 1. DASHBOARD SAYFASI
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // İstatistikler
        model.addAttribute("collectionRate", debtService.getCurrentCollectionRate());
        model.addAttribute("totalFlats", debtService.getTotalFlatCount());
        model.addAttribute("paidFlats", debtService.getPaidFlatCountForCurrentPeriod());
        model.addAttribute("currentBalance", BigDecimal.ZERO); // Örnek
        model.addAttribute("pendingComplaints", 0); // Örnek

        // Son Duyurular ve Ödemeler
        model.addAttribute("announcements", announcementService.getRecentActiveAnnouncements(5));
        model.addAttribute("recentPayments", paymentService.getRecentPaymentsForDashboard(3));

        model.addAttribute("currentPage", "dashboard");
        return "admin-dashboard";
    }

    // 2. ÖDEMELER SAYFASI (Eski AdminPaymentController buraya geldi)
    @GetMapping("/payments")
    public String showPayments(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        List<Payment> payments = paymentService.getRecentPaymentsForDashboard(50);
        model.addAttribute("payments", payments);

        model.addAttribute("currentPage", "payments");
        return "admin-payments";
    }

    // 3. DUYURULAR SAYFASI
    @GetMapping("/announcements")
    public String showAnnouncements(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("announcements", announcementService.getActiveAnnouncements());
        model.addAttribute("currentPage", "announcements");

        return "admin-announcements";
    }
}