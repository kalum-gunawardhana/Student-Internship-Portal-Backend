package com.internship.portal.service;

import com.internship.portal.dto.InternshipPostRequest;
import com.internship.portal.model.InternshipPost;
import com.internship.portal.model.PostStatus;
import com.internship.portal.model.User;
import com.internship.portal.repository.InternshipPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class InternshipService {
    @Autowired
    private InternshipPostRepository internshipPostRepository;

    @Autowired
    private EmailService emailService;

    public Page<InternshipPost> getActiveInternships(Pageable pageable, String search, String location) {
        if (search != null && location != null) {
            return internshipPostRepository.findByStatusAndSearchAndLocation(PostStatus.ACTIVE, search, location, pageable);
        } else if (search != null) {
            return internshipPostRepository.findByStatusAndSearch(PostStatus.ACTIVE, search, pageable);
        } else if (location != null) {
            return internshipPostRepository.findByStatusAndLocation(PostStatus.ACTIVE, location, pageable);
        } else {
            return internshipPostRepository.findByStatus(PostStatus.ACTIVE, pageable);
        }
    }

    public Optional<InternshipPost> getInternshipById(Long id) {
        return internshipPostRepository.findById(id);
    }

    public InternshipPost createInternship(InternshipPostRequest request, User company) {
        InternshipPost internship = new InternshipPost();
        internship.setTitle(request.getTitle());
        internship.setDescription(request.getDescription());
        internship.setLocation(request.getLocation());
        internship.setDuration(request.getDuration());
        internship.setRequirements(request.getRequirements());
        internship.setBenefits(request.getBenefits());
        internship.setStipend(request.getStipend());
        internship.setDeadline(request.getDeadline());
        internship.setCreatedBy(company);
        internship.setStatus(PostStatus.ACTIVE);

        InternshipPost saved = internshipPostRepository.save(internship);

        // Send notification email (optional)
        try {
            emailService.sendInternshipPostedNotification(saved);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        return saved;
    }

    public Page<InternshipPost> getInternshipsByCompany(User company, Pageable pageable) {
        return internshipPostRepository.findByCreatedBy(company, pageable);
    }

    public InternshipPost updateInternship(Long id, InternshipPostRequest request, User company) {
        InternshipPost internship = internshipPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getCreatedBy().getId().equals(company.getId())) {
            throw new RuntimeException("You can only update your own internships");
        }

        internship.setTitle(request.getTitle());
        internship.setDescription(request.getDescription());
        internship.setLocation(request.getLocation());
        internship.setDuration(request.getDuration());
        internship.setRequirements(request.getRequirements());
        internship.setBenefits(request.getBenefits());
        internship.setStipend(request.getStipend());
        internship.setDeadline(request.getDeadline());

        return internshipPostRepository.save(internship);
    }

    public InternshipPost updateInternshipStatus(Long id, PostStatus status, User company) {
        InternshipPost internship = internshipPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getCreatedBy().getId().equals(company.getId())) {
            throw new RuntimeException("You can only update your own internships");
        }

        internship.setStatus(status);
        return internshipPostRepository.save(internship);
    }

    public void deleteInternship(Long id, User company) {
        InternshipPost internship = internshipPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        if (!internship.getCreatedBy().getId().equals(company.getId())) {
            throw new RuntimeException("You can only delete your own internships");
        }

        internshipPostRepository.delete(internship);
    }

    // Admin methods
    public Page<InternshipPost> getAllInternships(Pageable pageable) {
        return internshipPostRepository.findAll(pageable);
    }

    public Page<InternshipPost> getInternshipsByStatus(PostStatus status, Pageable pageable) {
        return internshipPostRepository.findByStatus(status, pageable);
    }

    public InternshipPost adminUpdateInternshipStatus(Long id, PostStatus status) {
        InternshipPost internship = internshipPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Internship not found"));

        internship.setStatus(status);
        return internshipPostRepository.save(internship);
    }
}
