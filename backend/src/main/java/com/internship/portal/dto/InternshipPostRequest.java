package com.internship.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipPostRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String location;

    @NotBlank
    private String duration;

    private List<String> requirements;
    private List<String> benefits;
    private String stipend;

    @NotNull
    private LocalDateTime deadline;
}
