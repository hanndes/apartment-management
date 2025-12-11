package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.entities.Vehicle;
import com.group23.apartment_management.entities.dto.ComplaintDTO;
import com.group23.apartment_management.services.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DebtService debtService;
    private final PaymentService paymentService;
    private final AnnouncementService announcementService;
    private final VehicleService vehicleService;
    private final ResidentService residentService; // EKLENDİ: ResidentService'i enjekte ediyoruz
    private final ComplaintService complaintService;
    // --- GÜVENLİK KONTROLÜ ---
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }

    // 1. DASHBOARD
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("collectionRate", debtService.getCurrentCollectionRate());
        model.addAttribute("totalFlats", debtService.getTotalFlatCount());
        model.addAttribute("paidFlats", debtService.getPaidFlatCountForCurrentPeriod());
        model.addAttribute("currentBalance", BigDecimal.ZERO);
        model.addAttribute("pendingComplaints", 0);
        model.addAttribute("announcements", announcementService.getRecentActiveAnnouncements(5));
        model.addAttribute("recentPayments", paymentService.getRecentPaymentsForDashboard(3));
        model.addAttribute("currentPage", "dashboard");
        return "admin-dashboard";
    }

    // 2. ÖDEMELER
    @GetMapping("/payments")
    public String showPayments(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("payments", paymentService.getRecentPaymentsForDashboard(50));
        model.addAttribute("currentPage", "payments");
        return "admin-payments";
    }

    // 3. DUYURULAR
    @GetMapping("/announcements")
    public String showAnnouncements(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("announcements", announcementService.getActiveAnnouncements());
        model.addAttribute("currentPage", "announcements");
        return "admin-announcements";
    }

    // 4. ARAÇ YÖNETİMİ (GÜNCELLENDİ)
    @GetMapping("/vehicles")
    public String showVehicles(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // Araçları getir
        model.addAttribute("vehicles", vehicleService.getAllVehicles());

        // GÜNCELLEME: Sakinleri ResidentService üzerinden çekiyoruz
        // Bu sayede ResidentRepository'deki detaylı sorgu (isim + daire no) çalışıyor.
        model.addAttribute("residents", residentService.getAllResidents());

        model.addAttribute("newVehicle", new Vehicle());
        model.addAttribute("currentPage", "vehicles");
        return "admin-vehicles";
    }

    @PostMapping("/vehicles/add")
    public String addVehicle(@ModelAttribute Vehicle vehicle, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        vehicleService.addVehicle(vehicle);
        return "redirect:/admin/vehicles";
    }

    @GetMapping("/vehicles/delete/{id}")
    public String deleteVehicle(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        vehicleService.deleteVehicle(id);
        return "redirect:/admin/vehicles";
    }

    // --- ŞİKAYETLER SAYFASI (DÜZENLENMİŞ HALİ) ---
    @GetMapping("/complaints")
    public String showComplaints(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // BURASI ÖNEMLİ: Normal getAll() yerine DTO döndüren özel metodu çağırıyoruz
        List<ComplaintDTO> complaints = complaintService.getAllComplaintsWithNames();
        model.addAttribute("complaints", complaints);

        model.addAttribute("currentPage", "complaints");
        return "admin-complaints";
    }
}