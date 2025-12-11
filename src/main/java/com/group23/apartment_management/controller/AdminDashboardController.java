package com.group23.apartment_management.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group23.apartment_management.entities.Announcement;
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

        //1.aidat tahsilat oranı kartı 
        double collectionRate = debtService.getCurrentCollectionRate();
        model.addAttribute("collectionRate", collectionRate);

        //2.ödenen daire / toplam daire kartı
        int totalFlats = debtService.getTotalFlatCount();
        int paidFlats = debtService.getPaidFlatCountForCurrentPeriod();

        model.addAttribute("totalFlats", totalFlats);
        model.addAttribute("paidFlats", paidFlats);

        //3.bekleyen bakım kartı (şimdilik sıfır atadım complaintsService den çekeceğiz)
        int pendingComplaints = 0;
        model.addAttribute("pendingComplaints", pendingComplaints);

        //4.kasa bakiyesi kartı (şimdilik sıfır paymentService den çekeceğiz)
        BigDecimal currentBalance = BigDecimal.ZERO;
        model.addAttribute("currentBalance", currentBalance);

        //5.duyurular
        //List<Announcement> announcements = announcementService.getActiveAnnouncements();
        //model.addAttribute("announcements", announcements);

        List<Announcement> announcements =
                announcementService.getRecentActiveAnnouncements(5); // son 5 duyuru

        model.addAttribute("announcements", announcements);

        return "admin-dashboard";
    }
    
}
