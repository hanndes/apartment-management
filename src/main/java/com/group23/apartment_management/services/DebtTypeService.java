package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.DebtType;
import com.group23.apartment_management.repositories.DebtTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtTypeService {

    private final DebtTypeRepository debtTypeRepository;

    public List<DebtType> getAllDebtTypes() {
        return debtTypeRepository.findAll();
    }

    public DebtType getDebtTypeById(int id) {
        return debtTypeRepository.findById(id);
    }
}