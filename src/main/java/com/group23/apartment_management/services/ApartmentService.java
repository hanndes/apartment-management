package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.dto.ApartmentDropdownDTO;
import com.group23.apartment_management.repositories.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;

    /**
     * Verilen Blok ID'sine ait tüm daireleri getirir.
     * Gider dağıtımı (Expense Distribution) için kullanılır.
     */
    public List<ApartmentDropdownDTO> getApartmentsByBlockId(int blockId) {
        return apartmentRepository.findByBlockId(blockId);
    }
}