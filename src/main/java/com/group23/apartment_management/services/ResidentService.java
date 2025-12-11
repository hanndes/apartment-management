package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Resident;
import com.group23.apartment_management.repositories.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidentService {

    private final ResidentRepository residentRepository;

    // Tüm sakinleri getir (Admin panelinde listelemek için)
    public List<Resident> getAllResidents() {
        return residentRepository.findAllResidents();
    }

    // Yeni sakin ekle
    public boolean addResident(Resident resident) {
        return residentRepository.save(resident);
    }

    // Sakin sil (Pasife çek)
    public void deleteResident(int id) {
        residentRepository.delete(id);
    }
}