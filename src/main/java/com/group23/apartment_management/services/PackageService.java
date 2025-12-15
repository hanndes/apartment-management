package com.group23.apartment_management.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group23.apartment_management.entities.Package;
import com.group23.apartment_management.repositories.PackageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;

    public List<Package> getUserPackages(int userId) {
        return packageRepository.findPackagesByUserId(userId);
    }
    // Admin paneli için tüm kargolar
    public List<Package> getAllPackages() {
        return packageRepository.findAllWithDetails();
    }

    // Teslim edildi olarak işaretle
    public void markAsDelivered(int packageId) {
        packageRepository.updateDeliveryStatus(packageId, true);
    }
}