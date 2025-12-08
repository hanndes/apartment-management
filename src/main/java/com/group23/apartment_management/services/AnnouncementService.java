package com.group23.apartment_management.services;


import com.group23.apartment_management.entities.Announcement;
import com.group23.apartment_management.repositories.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findAllActive();
    }
    public List<Announcement> getUrgentAnnouncements() {
        return announcementRepository.findUrgentActive();
    }
}