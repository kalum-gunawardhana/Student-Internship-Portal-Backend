package com.internship.portal.service;

import com.internship.portal.dto.ApplicationRequest;
import com.internship.portal.model.Application;
import com.internship.portal.model.ApplicationStatus;
import com.internship.portal.model.InternshipPost;
import com.internship.portal.model.User;
import com.internship.portal.repository.ApplicationRepository;
import com.internship.portal.repository.InternshipPostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InternshipPostRepository internshipPostRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EmailService emailService;

    public Application applyForInternship(ApplicationRequest request, User student) {
        InternshipPost internship = internshipPostRepository.findById(request.getInternshipPostId())
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (applicationRepository.existsByStudentAndInternshipPost(student, internship)) {
            throw new RuntimeException("You have already applied for this internship");
        }

        Application application = new Application(student, internship);
        application.setCoverLetter(request.getCoverLetter());

        Application saved = applicationRepository.save(application);

        // Send notification emails
        try {
            emailService.sendApplicationSubmittedNotification(saved);
            emailService.sendNewApplicationNotification(saved);
        } catch (Exception e) {
            System.err.println("Failed to send email notifications: " + e.getMessage());
        }

        return saved;
    }

    public Application applyWithResume(Long internshipPostId, String coverLetter, MultipartFile resume, User student) {
        InternshipPost internship = internshipPostRepository.findById(internshipPostId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (applicationRepository.existsByStudentAndInternshipPost(student, internship)) {
            throw new RuntimeException("You have already applied for this internship");
        }

        String resumeFileName = null;
        if (resume != null && !resume.isEmpty()) {
            resumeFileName = fileStorageService.storeFile(resume, "resumes");
        }

        Application application = new Application(student, internship);
        application.setCoverLetter(coverLetter);
        application.setResumeFileName(resumeFileName);

        Application saved = applicationRepository.save(application);

        // Send notification emails
        try {
            emailService.sendApplicationSubmittedNotification(saved);
            emailService.sendNewApplicationNotification(saved);
        } catch (Exception e) {
            System.err.println("Failed to send email notifications: " + e.getMessage());
        }

        return saved;
    }

    public Page<Application> getApplicationsByStudent(User student, Pageable pageable) {
        return applicationRepository.findByStudent(student, pageable);
    }

    public Application withdrawApplication(Long id, User student) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("You can only withdraw your own applications");
        }

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("You can only withdraw pending applications");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        return applicationRepository.save(application);
    }

    public Page<Application> getApplicationsByCompany(User company, Pageable pageable) {
        return applicationRepository.findByCompany(company, pageable);
    }

    public Page<Application> getApplicationsByCompanyAndStatus(User company, ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findByCompanyAndStatus(company, status, pageable);
    }

    public Page<Application> getApplicationsForInternship(Long internshipId, User company, Pageable pageable) {
        InternshipPost internship = internshipPostRepository.findById(internshipId)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getCreatedBy().getId().equals(company.getId())) {
            throw new RuntimeException("You can only view applications for your own internships");
        }

        return applicationRepository.findByInternshipPost(internship, pageable);
    }

    public Application updateApplicationStatus(Long id, ApplicationStatus status, User company) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getInternshipPost().getCreatedBy().getId().equals(company.getId())) {
            throw new RuntimeException("You can only update applications for your own internships");
        }

        application.setStatus(status);
        Application saved = applicationRepository.save(application);

        // Send notification email
        try {
            emailService.sendApplicationStatusUpdateNotification(saved);
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        return saved;
    }

    // Admin methods
    public Page<Application> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    public Page<Application> getApplicationsByStatus(ApplicationStatus status, Pageable pageable) {
        return applicationRepository.findByStatus(status, pageable);
    }
}
