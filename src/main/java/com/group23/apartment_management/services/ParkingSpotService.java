package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.ParkingSpot;
import com.group23.apartment_management.repositories.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;

    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    public void addSpot(ParkingSpot spot) {
        parkingSpotRepository.save(spot);
    }

    public void deleteSpot(int id) {
        parkingSpotRepository.delete(id);
    }
}