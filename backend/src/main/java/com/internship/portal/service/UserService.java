package com.internship.portal.service;

import com.internship.portal.model.Role;
import com.internship.portal.model.User;
import com.internship.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUserProfile(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(updatedUser.getFullName());

        if (user.getRole() == Role.STUDENT) {
            user.setUniversity(updatedUser.getUniversity());
            user.setMajor(updatedUser.getMajor());
            user.setGraduationYear(updatedUser.getGraduationYear());
        } else if (user.getRole() == Role.COMPANY) {
            user.setCompanyName(updatedUser.getCompanyName());
            user.setIndustry(updatedUser.getIndustry());
            user.setWebsite(updatedUser.getWebsite());
            user.setDescription(updatedUser.getDescription());
        }

        return userRepository.save(user);
    }

    public String uploadResume(MultipartFile file, User student) {
        if (student.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can upload resumes");
        }

        String fileName = fileStorageService.storeFile(file, "resumes");
        student.setResumeFileName(fileName);
        userRepository.save(student);

        return fileName;
    }

    // Admin methods
    public Page<User> getAllUsers(Pageable pageable, Role role, String search) {
        if (role != null && search != null) {
            return userRepository.findByRoleAndSearch(role, search, pageable);
        } else if (role != null) {
            return userRepository.findByRole(role, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    public User toggleUserStatus(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot delete admin users");
        }

        userRepository.delete(user);
    }
}
