package com.group23.apartment_management.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group23.apartment_management.entities.Complaint;
import com.group23.apartment_management.entities.dto.ComplaintDTO;
import com.group23.apartment_management.entities.dto.ComplaintDetailDTO;
import com.group23.apartment_management.repositories.ComplaintRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public boolean createComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getUserComplaints(int userId) {
        return complaintRepository.findByUserId(userId);
    }

    public List<ComplaintDTO> getAllComplaintsWithNames() {
        return complaintRepository.findAllComplaintsWithNames();
    }


    public ComplaintDetailDTO getComplaintDetail(int id) {
        return complaintRepository.findComplaintDetailById(id);
    }


    public void respondToComplaint(int id, String response, String status) {
        complaintRepository.updateResponseAndStatus(id, response, status);
    }

    public int getPendingComplaintCount() {
        // "Bekliyor" durumundaki kayıtları saydır
        return complaintRepository.countByStatus("Bekliyor");
    }


    public int getInReviewComplaintCount() {
        return complaintRepository.countByStatus("İnceleniyor");
    }

    public int getResolvedThisMonthCount() {
        return complaintRepository.countResolvedThisMonth();
    }


}