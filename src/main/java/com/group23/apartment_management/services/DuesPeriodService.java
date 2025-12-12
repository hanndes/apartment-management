package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.DuesPeriod;
import com.group23.apartment_management.repositories.DuesPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DuesPeriodService {

    private final DuesPeriodRepository duesPeriodRepository;

    public List<DuesPeriod> getAllPeriods() {
        return duesPeriodRepository.findAll();
    }

    public void createPeriod(DuesPeriod period) {
        // Otomatik isim oluşturma (Örn: "Ocak 2025") - Eğer formdan gelmiyorsa
        if (period.getPeriodName() == null || period.getPeriodName().isEmpty()) {
            period.setPeriodName(period.getMonth() + "/" + period.getYear() + " Dönemi");
        }
        duesPeriodRepository.save(period);
    }

    public DuesPeriod getPeriodById(int id) {
        return duesPeriodRepository.findById(id);
    }
}