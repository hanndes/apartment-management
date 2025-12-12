package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.*;
import com.group23.apartment_management.entities.dto.ComplaintDTO;
import com.group23.apartment_management.entities.dto.ComplaintDetailDTO;
import com.group23.apartment_management.entities.dto.PaymentDTO;
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
    private final StaffService staffService;
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

// Import kısmına dikkat:
    // import com.group23.apartment_management.entities.PaymentDTO;

    @GetMapping("/payments")
    public String showPayments(HttpSession session, Model model) {
        // ... (Güvenlik kontrolü) ...

        // List<PaymentDTO> olarak alıyoruz
        List<
                PaymentDTO> payments = paymentService.getRecentPaymentsForDashboard(100);
        model.addAttribute("payments", payments);

        return "admin-payments";
    }

    // Dashboard metodunda da aynı şekilde PaymentDTO kullanılır.

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

    @GetMapping("/vehicles")
    public String showVehicles(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // Araçları getir
        model.addAttribute("vehicles", vehicleService.getAllVehicles());

        // HATA BURADAYDI: getAllResidents() yerine getAllResidentsDetailed() kullanıyoruz
        model.addAttribute("residents", residentService.getAllResidentsDetailed());

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

    // AdminController.java içine ekleyin:

    // DUYURU EKLEME İŞLEMİ
    @PostMapping("/announcements/add")
    public String addAnnouncement(@ModelAttribute Announcement announcement, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        // Tarihi şu anki zaman olarak ayarla
        announcement.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        announcement.setActive(true);

        announcementService.saveAnnouncement(announcement);

        return "redirect:/admin/announcements";
    }

    // DUYURU SİLME İŞLEMİ
    @GetMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        announcementService.deleteAnnouncement(id);

        return "redirect:/admin/announcements";
    }

    @GetMapping("/complaints/detail/{id}")
    public String showComplaintDetail(@PathVariable int id, HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        // ARTIK YENİ DTO'YU KULLANIYORUZ
        ComplaintDetailDTO complaint = complaintService.getComplaintDetail(id);

        model.addAttribute("complaint", complaint);

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        return "admin-complaint-detail";
    }

    @PostMapping("/complaints/respond")
    public String respondToComplaint(@RequestParam("complaintId") int id,
                                     @RequestParam("response") String response,
                                     @RequestParam("status") String status,
                                     HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        // Servis üzerinden güncelleme yap
        complaintService.respondToComplaint(id, response, status);

        // İşlem bitince detay sayfasına geri dön (Kullanıcı değişikliği görsün)
        return "redirect:/admin/complaints/detail/" + id;
    }
    // ... Diğer metodlar ...

    // --- SAKİNLER YÖNETİMİ ---
    @GetMapping("/residents")
    public String showResidents(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // Listeyi DTO olarak çekiyoruz
        model.addAttribute("residents", residentService.getAllResidentsDetailed());

        // Dropdownlar için veriler
        model.addAttribute("residentTypes", residentService.getResidentTypes());
        model.addAttribute("apartments", residentService.getApartmentsForDropdown());

        model.addAttribute("currentPage", "residents");
        return "admin-residents";
    }

    @PostMapping("/residents/add")
    public String addResident(@ModelAttribute Resident resident, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        residentService.addResident(resident);
        return "redirect:/admin/residents";
    }

    @GetMapping("/residents/delete/{id}")
    public String deleteResident(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        residentService.deleteResident(id);
        return "redirect:/admin/residents";
    }

    // --- PERSONEL YÖNETİMİ ---
    @GetMapping("/staff")
    public String showStaff(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("staffList", staffService.getAllStaff());
        model.addAttribute("currentPage", "staff");

        return "admin-staff";
    }

    @PostMapping("/staff/add")
    public String addStaff(@ModelAttribute Staff staff, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        staffService.addStaff(staff);
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/delete/{id}")
    public String deleteStaff(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        staffService.deleteStaff(id);
        return "redirect:/admin/staff";
    }


}