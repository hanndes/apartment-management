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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final PackageService packageService;
    private final DuesService duesService;

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        return user != null && "ADMIN".equals(user.getRole());
    }



    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("collectionRate", debtService.getCurrentCollectionRate());
        model.addAttribute("totalFlats", debtService.getTotalFlatCount());
        model.addAttribute("paidFlats", debtService.getPaidFlatCountForCurrentPeriod());
        model.addAttribute("currentBalance", debtService.getTotalPaidAmount());

        int pendingCount = complaintService.getPendingComplaintCount();
        model.addAttribute("pendingComplaints", pendingCount);

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

        List<PaymentDTO> payments = paymentService.getRecentPaymentsForDashboard(100);
        model.addAttribute("payments", payments);

        model.addAttribute("residents", residentService.getAllResidentsDetailed());

        model.addAttribute("currentPage", "payments");
        return "admin-payments";
    }

    @PostMapping("/payments/add")
    public String addPayment(@RequestParam("residentId") int residentId,
                            @RequestParam("amountPaid") java.math.BigDecimal amountPaid,
                            @RequestParam("paymentMethod") String paymentMethod,
                            @RequestParam(required = false) String referenceNo,
                            HttpSession session,
                            RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";

        Integer apartmentId = residentService.getApartmentIdByResidentId(residentId);
        if (apartmentId == null) {
            ra.addFlashAttribute("error", "Sakinin dairesi bulunamadı.");
            return "redirect:/admin/payments";
        }

        Debt debt = debtService.getFirstUnpaidDebtByApartmentId(apartmentId);
        if (debt == null) {
            ra.addFlashAttribute("error", "Bu sakin için ödenmemiş borç bulunamadı.");
            return "redirect:/admin/payments";
        }

        try {
            paymentService.processPaymentAmount(debt.getId(), residentId, amountPaid, paymentMethod, referenceNo);
            ra.addFlashAttribute("success", "Ödeme başarıyla kaydedildi.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Ödeme kaydedilirken hata oluştu.");
        }

        return "redirect:/admin/payments";
    }


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

    @GetMapping("/complaints")
    public String showComplaints(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        List<ComplaintDTO> complaints = complaintService.getAllComplaintsWithNames();
        model.addAttribute("complaints", complaints);
        model.addAttribute("pendingCount", complaintService.getPendingComplaintCount());
        model.addAttribute("inReviewCount", complaintService.getInReviewComplaintCount());
        model.addAttribute("resolvedThisMonthCount", complaintService.getResolvedThisMonthCount());


        model.addAttribute("currentPage", "complaints");
        return "admin-complaints";
    }



    @PostMapping("/announcements/add")
    public String addAnnouncement(@ModelAttribute Announcement announcement, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";


        announcement.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        announcement.setActive(true);

        announcementService.saveAnnouncement(announcement);

        return "redirect:/admin/announcements";
    }

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

        model.addAttribute("expenses", expenseService.getAllExpenses());
        model.addAttribute("categories", expenseService.getCategories());

        model.addAttribute("blocks", residentService.getAllBlocks());
        model.addAttribute("periods", duesPeriodService.getAllPeriods());
        model.addAttribute("debtTypes", debtTypeService.getAllDebtTypes());

        model.addAttribute("currentPage", "expenses");
        return "admin-expenses";
    }

    @PostMapping("/expenses/add")
    public String addExpense(@ModelAttribute Expense expense,
                             @RequestParam(required = false) Integer periodId,
                             @RequestParam(required = false) Integer debtTypeId,
                             @RequestParam(defaultValue = "false") boolean distribute,
                             HttpSession session) {

        if (!isAdmin(session)) return "redirect:/login";

        if (distribute && periodId != null && debtTypeId != null) {
            expenseService.addExpenseAndDistribute(expense, periodId, debtTypeId);
        } else {
            expenseService.addExpenseAndDistribute(expense, null, null);
        }

        return "redirect:/admin/expenses";
    }

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

        model.addAttribute("newSpot", new ParkingSpot());

        model.addAttribute("currentPage", "parking");
        return "admin-parking";
    }

    @PostMapping("/parking/add")
    public String addParking(@ModelAttribute("newSpot") ParkingSpot spot, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        System.out.println("DEBUG: Gelen Blok ID: " + spot.getBlockId());
        System.out.println("DEBUG: Gelen Park Kodu: " + spot.getSpotCode());
        try {
            parkingSpotService.addSpot(spot);
            ra.addFlashAttribute("success", "Park yeri eklendi.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Eklenemedi: " + e.getMessage());
        }
        return "redirect:/admin/parking";
    }

    @GetMapping("/parking/delete/{id}")
    public String deleteParking(@PathVariable int id, HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";
        parkingSpotService.deleteSpot(id);
        return "redirect:/admin/parking";
    }


    @GetMapping("/debts")
    public String showDebts(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("debts", debtService.getAllDebts());

        model.addAttribute("residents", residentService.getAllResidentsDetailed());
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


    @GetMapping("/packages")
    public String showPackages(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";

        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        try {
            model.addAttribute("packages", packageService.getAllPackages());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("packages", new java.util.ArrayList<>());
            model.addAttribute("error", "Kargolar listelenirken hata oluştu. Konsolu kontrol edin.");
        }

        model.addAttribute("currentPage", "packages");
        return "admin-packages";
    }

    @GetMapping("/packages/deliver/{id}")
    public String deliverPackage(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        if (!isAdmin(session)) return "redirect:/login";
        try {
            packageService.markAsDelivered(id);
            ra.addFlashAttribute("success", "Paket başarıyla teslim edildi.");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("error", "Paket teslim edilirken hata oluştu. Konsolu kontrol edin.");
        }
        return "redirect:/admin/packages";
    }

    @GetMapping("/debts/bulk-add")
    public String showBulkDuesPage(HttpSession session, Model model) {
        if (!isAdmin(session)) return "redirect:/login";
        User user = (User) session.getAttribute("loggedInUser");
        model.addAttribute("user", user);

        model.addAttribute("blocks", residentService.getAllBlocks());
        model.addAttribute("periods", duesPeriodService.getAllPeriods());
        model.addAttribute("debtTypes", debtTypeService.getAllDebtTypes());

        model.addAttribute("currentPage", "debts");
        return "admin-debts-bulk";
    }

    @PostMapping("/debts/bulk-add")
    public String processBulkDues(@RequestParam Integer blockId,
                                  @RequestParam Integer periodId,
                                  @RequestParam Integer debtTypeId,
                                  HttpSession session) {
        if (!isAdmin(session)) return "redirect:/login";

        duesService.applyDefinedDuesToDebts(blockId, periodId, debtTypeId);

        return "redirect:/admin/debts";
    }

}