package com.group23.apartment_management.services;


import java.util.List;

import org.springframework.stereotype.Service;

import com.group23.apartment_management.entities.Announcement;
import com.group23.apartment_management.repositories.AnnouncementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    // Tüm aktif duyurular (var olan)
    public List<Announcement> getActiveAnnouncements() {
        return announcementRepository.findAllActive();
    }
    // Acil / yüksek öncelikli duyurular (var olan)
    public List<Announcement> getUrgentAnnouncements() {
        return announcementRepository.findUrgentActive();
    }
    //Dashboard için son N aktif duyuru
    public List<Announcement> getRecentActiveAnnouncements(int limit) {
        List<Announcement> all = announcementRepository.findAllActive(); // tarih DESC geliyor
        if (all.size() <= limit) {
            return all;
        }
        return all.subList(0, limit);
    }
    // AnnouncementService.java içine ekleyin:

    public boolean saveAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(int id) {
        announcementRepository.delete(id);
    }
}