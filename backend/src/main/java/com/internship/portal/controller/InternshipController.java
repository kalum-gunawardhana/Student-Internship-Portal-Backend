package com.internship.portal.controller;

import com.internship.portal.dto.InternshipPostRequest;
import com.internship.portal.dto.MessageResponse;
import com.internship.portal.model.InternshipPost;
import com.internship.portal.model.PostStatus;
import com.internship.portal.model.User;
import com.internship.portal.service.InternshipService;
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
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/internships")
public class InternshipController {
    @Autowired
    private InternshipService internshipService;

    // Public endpoints for browsing internships
    @GetMapping("/public")
    public ResponseEntity<Page<InternshipPost>> getAllActiveInternships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<InternshipPost> internships = internshipService.getActiveInternships(pageable, search, location);
        return ResponseEntity.ok(internships);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<InternshipPost> getInternshipById(@PathVariable Long id) {
        Optional<InternshipPost> internship = internshipService.getInternshipById(id);
        return internship.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Company endpoints
    @PostMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> createInternship(@Valid @RequestBody InternshipPostRequest request,
                                              Authentication authentication) {
        User company = (User) authentication.getPrincipal();
        InternshipPost internship = internshipService.createInternship(request, company);
        return ResponseEntity.ok(internship);
    }

    @GetMapping("/company/my")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Page<InternshipPost>> getMyInternships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        User company = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InternshipPost> internships = internshipService.getInternshipsByCompany(company, pageable);
        return ResponseEntity.ok(internships);
    }

    @PutMapping("/company/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> updateInternship(@PathVariable Long id,
                                              @Valid @RequestBody InternshipPostRequest request,
                                              Authentication authentication) {
        User company = (User) authentication.getPrincipal();
        try {
            InternshipPost updated = internshipService.updateInternship(id, request, company);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/company/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> updateInternshipStatus(@PathVariable Long id,
                                                    @RequestParam PostStatus status,
                                                    Authentication authentication) {
        User company = (User) authentication.getPrincipal();
        try {
            InternshipPost updated = internshipService.updateInternshipStatus(id, status, company);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/company/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> deleteInternship(@PathVariable Long id, Authentication authentication) {
        User company = (User) authentication.getPrincipal();
        try {
            internshipService.deleteInternship(id, company);
            return ResponseEntity.ok(new MessageResponse("Internship deleted successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<InternshipPost>> getAllInternships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) PostStatus status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<InternshipPost> internships = status != null ?
                internshipService.getInternshipsByStatus(status, pageable) :
                internshipService.getAllInternships(pageable);
        return ResponseEntity.ok(internships);
    }

    @PutMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminUpdateInternshipStatus(@PathVariable Long id,
                                                         @RequestParam PostStatus status) {
        try {
            InternshipPost updated = internshipService.adminUpdateInternshipStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
