package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.Complaint;
import com.group23.apartment_management.entities.Payment;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.*; // Tüm servisleri kapsar
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class ResidentController {

    private final AnnouncementService announcementService;
    private final PackageService packageService;
    private final ComplaintService complaintService;
    private final PaymentService paymentService;
    private final VehicleService vehicleService;
    private final DebtService debtService; // EKLENDİ: Borçları listelemek için şart!

    // --- GÜVENLİK KONTROLÜ ---
    private boolean isResident(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "RESIDENT".equals(user.getRole());
    }

    // 1. SAKİN DASHBOARD
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // Verileri Yükle
        model.addAttribute("announcements", announcementService.getUrgentAnnouncements());
        model.addAttribute("packages", packageService.getUserPackages(user.getId()));
        model.addAttribute("complaints", complaintService.getUserComplaints(user.getId()));

        // Kullanıcının araçlarını getir
        model.addAttribute("vehicles", vehicleService.getVehiclesByUserId(user.getId()));

        // Son Ödemeleri Çek (Dashboard tablosu için)
        List<Payment> myPayments = paymentService.getUserPayments(user.getId());
        model.addAttribute("recentPayments", myPayments);

        model.addAttribute("currentPage", "dashboard");
        return "user-dashboard";
    }

    // 2. ÖDEMELERİM SAYFASI (Borç Listesi)
    @GetMapping("/payments")
    public String showMyPayments(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // ÖNEMLİ GÜNCELLEME: Burada payments değil, DEBTS (Borçlar) çekiyoruz.
        // Çünkü HTML sayfasında th:each="debt : ${debts}" yapısını kurduk.
        model.addAttribute("debts", debtService.getUserDebts(user.getId()));

        model.addAttribute("currentPage", "payments");
        return "user-payments";
    }

    // YENİ EKLENEN: ÖDEME YAPMA İŞLEMİ
    @PostMapping("/payments/pay")
    public String makePayment(@RequestParam("debtId") int debtId, HttpSession session) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");

        // Servis üzerinden ödeme işlemini gerçekleştir (Borcu kapat, ödeme kaydı oluştur)
        paymentService.processPayment(debtId, user.getId());

        // İşlem bitince sayfayı yenile
        return "redirect:/user/payments";
    }

    // 3. ŞİKAYETLER SAYFASI
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

    // 4. ŞİKAYET EKLEME İŞLEMİ
    @PostMapping("/complaints/add")
    public String addComplaint(@ModelAttribute Complaint complaint, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            complaint.setUserId(user.getId());
            complaintService.createComplaint(complaint);
        }
        return "redirect:/user/complaints";
    }

    // 5. DUYURULAR SAYFASI
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