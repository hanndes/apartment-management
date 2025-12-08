package com.group23.apartment_management.controller;

import com.group23.apartment_management.entities.Announcement;
import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.entities.Package;
import com.group23.apartment_management.services.AnnouncementService;
import com.group23.apartment_management.services.PackageService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserDashboardController {

    private final AnnouncementService announcementService;
    private final PackageService packageService;

    @GetMapping("/user/dashboard")
    public String showUserDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"RESIDENT".equals(user.getRole())) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        List<Announcement> urgentAnnouncements = announcementService.getUrgentAnnouncements();
        model.addAttribute("announcements", urgentAnnouncements);

        List<Package> packages = packageService.getUserPackages(user.getId());
        model.addAttribute("packages", packages);

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
}