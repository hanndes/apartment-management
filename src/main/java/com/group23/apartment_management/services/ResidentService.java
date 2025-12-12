package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Block;
import com.group23.apartment_management.entities.Resident;
import com.group23.apartment_management.entities.ResidentType;
import com.group23.apartment_management.entities.dto.ApartmentDropdownDTO;
import com.group23.apartment_management.entities.dto.ResidentDTO;
import com.group23.apartment_management.repositories.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidentService {

    private final ResidentRepository residentRepository;

    public List<ResidentDTO> getAllResidentsDetailed() {
        return residentRepository.findAllResidentsWithDetails();
    }

    public void addResident(Resident resident) {
        residentRepository.save(resident);
    }

    public void deleteResident(int id) {
        residentRepository.delete(id);
    }

    // ResidentService.java içine ekleyin:

    public List<Block> getAllBlocks() {
        return residentRepository.findAllBlocks();
    }
    // Dropdownlar için veriler
    public List<ResidentType> getResidentTypes() {
        return residentRepository.findAllTypes();
    }

    public List<ApartmentDropdownDTO> getApartmentsForDropdown() {
        return residentRepository.findAllApartmentsForDropdown();
    }

    public Integer getApartmentIdByResidentId(int residentId) {
        return residentRepository.findApartmentIdByResidentId(residentId);
    }
}