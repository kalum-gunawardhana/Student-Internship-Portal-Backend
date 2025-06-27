package com.internship.portal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internship_posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    @Column(length = 2000)
    private String description;

    @NotBlank
    private String location;

    @NotBlank
    private String duration;

    @ElementCollection
    @CollectionTable(name = "internship_requirements", joinColumns = @JoinColumn(name = "internship_id"))
    @Column(name = "requirement")
    private List<String> requirements = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "internship_benefits", joinColumns = @JoinColumn(name = "internship_id"))
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();

    private String stipend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @NotNull
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "internshipPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();
}
