package com.internship.portal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_post_id", nullable = false)
    private InternshipPost internshipPost;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(length = 1000)
    private String coverLetter;

    private String resumeFileName;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ✅ Custom constructor for simplified object creation
    public Application(User student, InternshipPost internshipPost) {
        this.student = student;
        this.internshipPost = internshipPost;
        this.status = ApplicationStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
    }
}