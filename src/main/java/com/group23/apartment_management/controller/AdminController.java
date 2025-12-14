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
    private final ResidentService residentService; 
    private final ComplaintService complaintService;
    private final StaffService staffService;
    private final ParkingSpotService parkingSpotService;
    private final DebtTypeService debtTypeService;
    private final DuesPeriodService duesPeriodService;

    //guvenlik kontrolu
    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }



    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        //aggregation ile hesaplanan veriler
        // 1. Borç/Aidat İstatistikleri
        model.addAttribute("collectionRate", debtService.getCurrentCollectionRate());
        model.addAttribute("totalFlats", debtService.getTotalFlatCount());
        model.addAttribute("paidFlats", debtService.getPaidFlatCountForCurrentPeriod());
        model.addAttribute("currentBalance", debtService.getTotalPaidAmount()); // SUM(amount)

        // 2. Bekleyen Şikayet Sayısı 
        int pendingCount = complaintService.getPendingComplaintCount();
        model.addAttribute("pendingComplaints", pendingCount);

        //Diğer veriler (Duyurular, Son Ödemeler)
        model.addAttribute("announcements", announcementService.getRecentActiveAnnouncements(5));
        model.addAttribute("recentPayments", paymentService.getRecentPaymentsForDashboard(3));

        model.addAttribute("currentPage", "dashboard");
        return "admin-dashboard";
    }


    @GetMapping("/payments")
    public String showPayments(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // 1. Ödemeler Listesi
        List<PaymentDTO> payments = paymentService.getRecentPaymentsForDashboard(100);
        model.addAttribute("payments", payments);

        model.addAttribute("residents", residentService.getAllResidentsDetailed());

        model.addAttribute("currentPage", "payments");
        return "admin-payments";
    }
    

    //duyurular
    @GetMapping("/announcements")
    public String showAnnouncements(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("announcements", announcementService.getActiveAnnouncements());
        model.addAttribute("currentPage", "announcements");
        return "admin-announcements";
    }

    //araclar
    @GetMapping("/vehicles")
    public String showVehicles(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // Araçları getir
        model.addAttribute("vehicles", vehicleService.getAllVehicles());

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

    //sikayetler
    @GetMapping("/complaints")
    public String showComplaints(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        List<ComplaintDTO> complaints = complaintService.getAllComplaintsWithNames();
        model.addAttribute("complaints", complaints);

        model.addAttribute("currentPage", "complaints");
        return "admin-complaints";
    }



    //duyuru ekleme islemi
    @PostMapping("/announcements/add")
    public String addAnnouncement(@ModelAttribute Announcement announcement, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        
        announcement.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        announcement.setActive(true);

        announcementService.saveAnnouncement(announcement);

        return "redirect:/admin/announcements";
    }

    //duyuru silme islemi 
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

        
        complaintService.respondToComplaint(id, response, status);

        
        return "redirect:/admin/complaints/detail/" + id;
    }
   

    //sakinler yonetimi
    @GetMapping("/residents")
    public String showResidents(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        
        model.addAttribute("residents", residentService.getAllResidentsDetailed());

        
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

    //personel yonetimi
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

    private final UserService userService;



    //kullanicilar yonetimi
    @GetMapping("/users")
    public String showUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        
        model.addAttribute("users", userService.getAllUsers());

        model.addAttribute("residents", residentService.getAllResidentsDetailed());

        model.addAttribute("currentPage", "users");
        return "admin-users";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute User newUser,
                          @RequestParam(required = false) Integer residentId, 
                          HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        userService.addUser(newUser, residentId);

        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    private final ExpenseService expenseService;


    @GetMapping("/expenses")
    public String showExpenses(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        //Mevcut Giderler ve Kategoriler
        model.addAttribute("expenses", expenseService.getAllExpenses());
        model.addAttribute("categories", expenseService.getCategories());

        //yeni eklenenler
        model.addAttribute("blocks", residentService.getAllBlocks());       // Blok seçimi için
        model.addAttribute("periods", duesPeriodService.getAllPeriods());   // Hangi döneme borç yazılacak?
        model.addAttribute("debtTypes", debtTypeService.getAllDebtTypes()); // Borç türü (Ortak Gider vb.)

        model.addAttribute("currentPage", "expenses");
        return "admin-expenses";
    }

    //Gider Ekle 
    @PostMapping("/expenses/add")
    public String addExpense(@ModelAttribute Expense expense,
                             @RequestParam(required = false) Integer periodId,   // Formdan gelen Dönem ID
                             @RequestParam(required = false) Integer debtTypeId, // Formdan gelen Borç Türü ID
                             @RequestParam(defaultValue = "false") boolean distribute, // "Dağıt" seçeneği işaretli mi?
                             HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        // Eğer kullanıcı "Dağıt" kutusunu işaretlediyse ve gerekli bilgiler varsa
        if (distribute && periodId != null && debtTypeId != null) {
            // Hem gideri kaydet hem de dairelere borç olarak dağıt
            expenseService.addExpenseAndDistribute(expense, periodId, debtTypeId);
        } else {
            // Sadece gider olarak kaydet (Dağıtım yapma)
            // Bu durumda periodId ve debtTypeId null gönderilir
            expenseService.addExpenseAndDistribute(expense, null, null);
        }

        return "redirect:/admin/expenses";
    }

    // Gider Sil
    @GetMapping("/expenses/delete/{id}")
    public String deleteExpense(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        expenseService.deleteExpense(id);
        return "redirect:/admin/expenses";
    }
    @GetMapping("/parking")
    public String showParking(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("parkingSpots", parkingSpotService.getAllSpots());

        model.addAttribute("blocks", residentService.getAllBlocks());

        model.addAttribute("currentPage", "parking");
        return "admin-parking";
    }

    @PostMapping("/parking/add")
    public String addParking(@ModelAttribute ParkingSpot spot, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        parkingSpotService.addSpot(spot);
        return "redirect:/admin/parking";
    }

    @GetMapping("/parking/delete/{id}")
    public String deleteParking(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        parkingSpotService.deleteSpot(id);
        return "redirect:/admin/parking";
    }


    //borc/aidat yonetimi
    @GetMapping("/debts")
    public String showDebts(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        // Tablo verisi
        model.addAttribute("debts", debtService.getAllDebts());

        // Dropdown verileri
        model.addAttribute("residents", residentService.getAllResidentsDetailed()); // Kime borç yazılacak?
        model.addAttribute("periods", duesPeriodService.getAllPeriods());           // Hangi dönem?
        model.addAttribute("types", debtTypeService.getAllDebtTypes());             // Hangi tür? (Aidat/Yakıt)

        model.addAttribute("currentPage", "debts"); 
        return "admin-debts";
    }

    @PostMapping("/debts/add")
    public String addDebt(@ModelAttribute Debt debt,
                          @RequestParam("residentId") int residentId,
                          HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        // Seçilen Sakinin Dairesini Bul
        Integer apartmentId = residentService.getApartmentIdByResidentId(residentId);
        if (apartmentId != null) {
            debt.setApartmentId(apartmentId);
            debtService.addDebt(debt);
        }

        return "redirect:/admin/debts";
    }

    @GetMapping("/debts/delete/{id}")
    public String deleteDebt(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        debtService.deleteDebt(id);
        return "redirect:/admin/debts";
    }



}