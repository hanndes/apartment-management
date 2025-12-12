package com.group23.apartment_management.services;

import com.group23.apartment_management.config.DatabaseConnection;
import com.group23.apartment_management.entities.Vehicle;
import com.group23.apartment_management.repositories.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

// VehicleRepository.java dosyasının en altına (sınıfın içine) ekleyin:

    // VehicleService.java dosyasının içine ekleyin:

// VehicleService.java içindeki metodu güncelleyin:

    public List<Vehicle> getVehiclesByUserId(int userId) {
        return vehicleRepository.findVehiclesByUserId(userId);
    }
}