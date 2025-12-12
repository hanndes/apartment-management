package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Staff;
import com.group23.apartment_management.repositories.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffService {
    private final StaffRepository staffRepository;

    public List<Staff> getAllStaff() {
        return staffRepository.findAllActive();
    }

    public void addStaff(Staff staff) {
        staffRepository.save(staff);
    }

    public void deleteStaff(int id) {
        staffRepository.delete(id);
    }
}