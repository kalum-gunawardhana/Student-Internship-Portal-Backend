package com.internship.portal.config;

import com.internship.portal.model.Role;
import com.internship.portal.model.User;
import com.internship.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@portal.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        // Create sample student if not exists
        if (!userRepository.existsByUsername("john_student")) {
            User student = new User();
            student.setUsername("john_student");
            student.setEmail("john@student.com");
            student.setPassword(passwordEncoder.encode("password123"));
            student.setFullName("John Smith");
            student.setRole(Role.STUDENT);
            student.setUniversity("MIT");
            student.setMajor("Computer Science");
            student.setGraduationYear(2025);
            userRepository.save(student);
        }

        // Create sample company if not exists
        if (!userRepository.existsByUsername("tech_corp")) {
            User company = new User();
            company.setUsername("tech_corp");
            company.setEmail("hr@techcorp.com");
            company.setPassword(passwordEncoder.encode("password123"));
            company.setFullName("TechCorp HR");
            company.setRole(Role.COMPANY);
            company.setCompanyName("TechCorp Solutions");
            company.setIndustry("Technology");
            company.setWebsite("https://techcorp.com");
            company.setDescription("Leading technology solutions provider");
            userRepository.save(company);
        }
    }
}
