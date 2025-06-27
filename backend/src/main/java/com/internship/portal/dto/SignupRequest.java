package com.internship.portal.dto;

import com.internship.portal.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotBlank
    private String fullName;

    private Role role;

    // Student specific fields
    private String university;
    private String major;
    private Integer graduationYear;

    // Company specific fields
    private String companyName;
    private String industry;
    private String website;
    private String description;
}
