package com.internship.portal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {
    @NotNull
    private Long internshipPostId;

    private String coverLetter;
}
