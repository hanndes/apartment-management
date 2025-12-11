package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Vehicle;
import com.group23.apartment_management.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAllVehicles();
    }

    public boolean addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(int id) {
        vehicleRepository.delete(id);
    }

    // ResidentService'e ihtiyaç duyacağız (Kullanıcı listesini dropdown'a doldurmak için)
    // Eğer ResidentService yoksa onu da inject etmeniz gerekebilir.
}