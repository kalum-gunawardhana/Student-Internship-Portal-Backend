package com.internship.portal.controller;

import com.internship.portal.dto.ApplicationRequest;
import com.internship.portal.dto.MessageResponse;
import com.internship.portal.model.Application;
import com.internship.portal.model.ApplicationStatus;
import com.internship.portal.model.User;
import com.internship.portal.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    // Student endpoints
    @PostMapping("/student/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> applyForInternship(@Valid @RequestBody ApplicationRequest request,
                                                Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        try {
            Application application = applicationService.applyForInternship(request, student);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/student/apply-with-resume")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> applyWithResume(@RequestParam Long internshipPostId,
                                             @RequestParam(required = false) String coverLetter,
                                             @RequestParam("resume") MultipartFile resume,
                                             Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        try {
            Application application = applicationService.applyWithResume(internshipPostId, coverLetter, resume, student);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/student/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Page<Application>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        User student = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> applications = applicationService.getApplicationsByStudent(student, pageable);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/student/{id}/withdraw")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> withdrawApplication(@PathVariable Long id, Authentication authentication) {
        User student = (User) authentication.getPrincipal();
        try {
            Application application = applicationService.withdrawApplication(id, student);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Company endpoints
    @GetMapping("/company/received")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Page<Application>> getReceivedApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ApplicationStatus status,
            Authentication authentication) {

        User company = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> applications = status != null ?
                applicationService.getApplicationsByCompanyAndStatus(company, status, pageable) :
                applicationService.getApplicationsByCompany(company, pageable);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/company/internship/{internshipId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Page<Application>> getApplicationsForInternship(
            @PathVariable Long internshipId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        User company = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        try {
            Page<Application> applications = applicationService.getApplicationsForInternship(internshipId, company, pageable);
            return ResponseEntity.ok(applications);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/company/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long id,
                                                     @RequestParam ApplicationStatus status,
                                                     Authentication authentication) {
        User company = (User) authentication.getPrincipal();
        try {
            Application application = applicationService.updateApplicationStatus(id, status, company);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Application>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ApplicationStatus status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> applications = status != null ?
                applicationService.getApplicationsByStatus(status, pageable) :
                applicationService.getAllApplications(pageable);
        return ResponseEntity.ok(applications);
    }
}
