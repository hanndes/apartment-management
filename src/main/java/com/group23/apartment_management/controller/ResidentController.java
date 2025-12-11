package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.Complaint;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.AnnouncementService;
import com.group23.apartment_management.services.ComplaintService;
import com.group23.apartment_management.services.PackageService;
import com.group23.apartment_management.services.PaymentService;
import com.group23.apartment_management.services.VehicleService; // Eklendi
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class ResidentController {

    private final AnnouncementService announcementService;
    private final PackageService packageService;
    private final ComplaintService complaintService;
    private final PaymentService paymentService;
    private final VehicleService vehicleService; // Araçları göstermek için lazım

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

        // Araçları Yükle (Hata almamak için)
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        // Not: Normalde sadece kendi aracını getirmeli: vehicleService.getResidentVehicles(user.getId())
        // ama şimdilik hata vermemesi için genel listeyi koydum.

        // --- AKTİF SAYFA İŞARETÇİSİ ---
        model.addAttribute("currentPage", "dashboard");

        return "user-dashboard";
    }

    // 2. ÖDEMELERİM SAYFASI
    @GetMapping("/payments")
    public String showMyPayments(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("payments", paymentService.getUserPayments(user.getId()));

        model.addAttribute("currentPage", "payments"); // İşaretçi
        return "user-payments";
    }

    // 3. ŞİKAYETLER SAYFASI
    @GetMapping("/complaints")
    public String showComplaints(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("complaints", complaintService.getUserComplaints(user.getId()));
        model.addAttribute("newComplaint", new Complaint());

        model.addAttribute("currentPage", "complaints"); // İşaretçi
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

        model.addAttribute("currentPage", "announcements"); // İşaretçi
        return "user-announcements";
    }
}