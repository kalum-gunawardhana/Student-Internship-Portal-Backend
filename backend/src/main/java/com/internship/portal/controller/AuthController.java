package com.internship.portal.controller;

import com.internship.portal.dto.JwtResponse;
import com.internship.portal.dto.LoginRequest;
import com.internship.portal.dto.MessageResponse;
import com.internship.portal.dto.SignupRequest;
import com.internship.portal.model.Role;
import com.internship.portal.model.User;
import com.internship.portal.repository.UserRepository;
import com.internship.portal.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getFullName(),
                userDetails.getRole()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFullName(),
                signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.STUDENT);

        // Set role-specific fields
        if (user.getRole() == Role.STUDENT) {
            user.setUniversity(signUpRequest.getUniversity());
            user.setMajor(signUpRequest.getMajor());
            user.setGraduationYear(signUpRequest.getGraduationYear());
        } else if (user.getRole() == Role.COMPANY) {
            user.setCompanyName(signUpRequest.getCompanyName());
            user.setIndustry(signUpRequest.getIndustry());
            user.setWebsite(signUpRequest.getWebsite());
            user.setDescription(signUpRequest.getDescription());
        }

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
