package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Complaint;
import com.group23.apartment_management.repositories.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}