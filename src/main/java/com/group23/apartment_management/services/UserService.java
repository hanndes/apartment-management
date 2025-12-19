package com.group23.apartment_management.services;

import com.group23.apartment_management.entities.User;
import com.group23.apartment_management.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password) && user.isActive()) {
            return user;
        }

        return null;
    }

    public boolean hasRole(User user, String role) {
        return user.getRole() != null && user.getRole().equals(role);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addUser(User user, Integer residentId) {
        int newUserId = userRepository.save(user);

        if (newUserId != -1 && residentId != null) {
            userRepository.linkUserToResident(newUserId, residentId);
        }
    }

    public void deleteUser(int id) {
        userRepository.delete(id);
    }
}