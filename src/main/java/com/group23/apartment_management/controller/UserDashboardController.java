package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.Announcement;
import com.group23.apartment_management.entities.Complaint;
import com.group23.apartment_management.entities.Package;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.services.AnnouncementService;
import com.group23.apartment_management.services.ComplaintService;
import com.group23.apartment_management.services.PackageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserDashboardController {

    private final AnnouncementService announcementService;
    private final PackageService packageService;
    private final ComplaintService complaintService;

    @GetMapping("/user/dashboard")
    public String showUserDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"RESIDENT".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        // 1. DUYURULAR
        List<Announcement> urgentAnnouncements = announcementService.getUrgentAnnouncements();
        model.addAttribute("announcements", urgentAnnouncements);

        // 2. KARGOLAR
        List<Package> packages = packageService.getUserPackages(user.getId());
        model.addAttribute("packages", packages);

        // --- 3. YENİ EKLENEN KISIM: ŞİKAYETLER ---
        // Kullanıcının şikayetlerini listeye ekliyoruz
        List<Complaint> complaints = complaintService.getUserComplaints(user.getId());
        model.addAttribute("complaints", complaints);
        // -----------------------------------------

        return "user-dashboard";
    }

    @GetMapping("/user/announcements")
    public String showAnnouncementsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"RESIDENT".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        List<Announcement> allAnnouncements = announcementService.getActiveAnnouncements();
        model.addAttribute("announcements", allAnnouncements);

        return "user-announcements";
    }

    @GetMapping("/user/complaints")
    public String showComplaintsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"RESIDENT".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        List<Complaint> complaints = complaintService.getUserComplaints(user.getId());
        model.addAttribute("complaints", complaints);

        model.addAttribute("newComplaint", new Complaint());

        return "user-complaints";
    }

    @PostMapping("/user/complaints/add")
    public String addComplaint(@ModelAttribute Complaint complaint, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user != null) {

            complaint.setUserId(user.getId());

            complaintService.createComplaint(complaint);
        }

        return "redirect:/user/complaints";
    }
}