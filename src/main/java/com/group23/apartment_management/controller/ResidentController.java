package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.Complaint;
<<<<<<< HEAD
import com.group23.apartment_management.entities.Debt;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.AnnouncementService;
import com.group23.apartment_management.services.ComplaintService;
import com.group23.apartment_management.services.DebtService;
import com.group23.apartment_management.services.PackageService;
import com.group23.apartment_management.services.PaymentService;
import com.group23.apartment_management.services.VehicleService; 
=======
import com.group23.apartment_management.entities.Payment;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.*; // Tüm servisleri kapsar
>>>>>>> b6eb3c629dba92cb79f2386b783546305bc02da5
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
=======
import org.springframework.web.bind.annotation.*;

import java.util.List;
>>>>>>> b6eb3c629dba92cb79f2386b783546305bc02da5

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class ResidentController {

    @Autowired
    private DebtService debtService;


    private final AnnouncementService announcementService;
    private final PackageService packageService;
    private final ComplaintService complaintService;
    private final PaymentService paymentService;
<<<<<<< HEAD
    private final VehicleService vehicleService; 
=======
    private final VehicleService vehicleService;
    private final DebtService debtService; // EKLENDİ: Borçları listelemek için şart!
>>>>>>> b6eb3c629dba92cb79f2386b783546305bc02da5

    //guvenlik kontrolu
    private boolean isResident(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "RESIDENT".equals(user.getRole());
    }

    //sakin dashboard
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        //verileri yukle
        model.addAttribute("announcements", announcementService.getUrgentAnnouncements());
        model.addAttribute("packages", packageService.getUserPackages(user.getId()));
        model.addAttribute("complaints", complaintService.getUserComplaints(user.getId()));

<<<<<<< HEAD
        
        model.addAttribute("vehicles", vehicleService.getVehiclesByUserId(user.getId()));

        //aktif sayfa isaretcisi
        model.addAttribute("currentPage", "dashboard");
=======
        // Kullanıcının araçlarını getir
        model.addAttribute("vehicles", vehicleService.getVehiclesByUserId(user.getId()));

        // Son Ödemeleri Çek (Dashboard tablosu için)
        List<Payment> myPayments = paymentService.getUserPayments(user.getId());
        model.addAttribute("recentPayments", myPayments);
>>>>>>> b6eb3c629dba92cb79f2386b783546305bc02da5

        model.addAttribute("currentPage", "dashboard");
        return "user-dashboard";
    }

<<<<<<< HEAD
    //odemelerim sayfasi
=======
    // 2. ÖDEMELERİM SAYFASI (Borç Listesi)
>>>>>>> b6eb3c629dba92cb79f2386b783546305bc02da5
    @GetMapping("/payments")
    public String showMyPayments(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // ÖNEMLİ GÜNCELLEME: Burada payments değil, DEBTS (Borçlar) çekiyoruz.
        // Çünkü HTML sayfasında th:each="debt : ${debts}" yapısını kurduk.
        model.addAttribute("debts", debtService.getUserDebts(user.getId()));

<<<<<<< HEAD
        model.addAttribute("currentPage", "payments"); 
        return "user-payments";
    }

    //sikayetler sayfasi
=======
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
>>>>>>> b6eb3c629dba92cb79f2386b783546305bc02da5
    @GetMapping("/complaints")
    public String showComplaints(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("complaints", complaintService.getUserComplaints(user.getId()));
        model.addAttribute("newComplaint", new Complaint());

<<<<<<< HEAD
        model.addAttribute("currentPage", "complaints"); 
=======
        model.addAttribute("currentPage", "complaints");
>>>>>>> b6eb3c629dba92cb79f2386b783546305bc02da5
        return "user-complaints";
    }

    //sikayet ekleme
    @PostMapping("/complaints/add")
    public String addComplaint(@ModelAttribute Complaint complaint, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            complaint.setUserId(user.getId());
            complaintService.createComplaint(complaint);
        }
        return "redirect:/user/complaints";
    }

    //duyurular sayfasi
    @GetMapping("/announcements")
    public String showAnnouncementsPage(HttpSession session, Model model) {
        if (!isResident(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("announcements", announcementService.getActiveAnnouncements());

<<<<<<< HEAD
        model.addAttribute("currentPage", "announcements"); 
        return "user-announcements";
    }



    @GetMapping("/aidat-ode")
public String aidatOdePage(HttpSession session, Model model) {

    if (!isResident(session)) {
        return "redirect:/login";
    }

    User user = (User) session.getAttribute("loggedInUser");

    model.addAttribute(
        "debts",
        debtService.getUnpaidDebtsByUserId(user.getId())
    );

    model.addAttribute("currentPage", "aidat");

    return "user-aidatOde";
}
@PostMapping("/pay")
public String payDebt(@RequestParam int debtId) {

    debtService.updateDebtAfterPayment(
        debtId,
        BigDecimal.ZERO,
        true
    );

    return "redirect:/user/aidat-ode";
}



=======
        model.addAttribute("currentPage", "announcements");
        return "user-announcements";
    }
>>>>>>> b6eb3c629dba92cb79f2386b783546305bc02da5
}