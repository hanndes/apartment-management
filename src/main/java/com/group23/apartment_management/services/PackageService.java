package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.Package;
import com.group23.apartment_management.repositories.PackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;

    public List<Package> getUserPackages(int userId) {
        return packageRepository.findPackagesByUserId(userId);
    }
}