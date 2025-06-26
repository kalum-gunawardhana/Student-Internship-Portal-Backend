package com.internship.portal.service;

import com.internship.portal.model.Application;
import com.internship.portal.model.InternshipPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendInternshipPostedNotification(InternshipPost internship) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(internship.getCreatedBy().getEmail());
        message.setSubject("Internship Posted Successfully - " + internship.getTitle());
        message.setText("Dear " + internship.getCreatedBy().getFullName() + ",\n\n" +
                "Your internship posting '" + internship.getTitle() + "' has been successfully created and is now live on our platform.\n\n" +
                "Students can now view and apply for this position.\n\n" +
                "Best regards,\n" +
                "Student Internship Portal Team");

        emailSender.send(message);
    }

    public void sendApplicationSubmittedNotification(Application application) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(application.getStudent().getEmail());
        message.setSubject("Application Submitted - " + application.getInternshipPost().getTitle());
        message.setText("Dear " + application.getStudent().getFullName() + ",\n\n" +
                "Your application for '" + application.getInternshipPost().getTitle() + "' at " +
                application.getInternshipPost().getCreatedBy().getCompanyName() + " has been successfully submitted.\n\n" +
                "You will be notified once the company reviews your application.\n\n" +
                "Best regards,\n" +
                "Student Internship Portal Team");

        emailSender.send(message);
    }

    public void sendNewApplicationNotification(Application application) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(application.getInternshipPost().getCreatedBy().getEmail());
        message.setSubject("New Application Received - " + application.getInternshipPost().getTitle());
        message.setText("Dear " + application.getInternshipPost().getCreatedBy().getFullName() + ",\n\n" +
                "You have received a new application for your internship posting '" + application.getInternshipPost().getTitle() + "'.\n\n" +
                "Applicant: " + application.getStudent().getFullName() + "\n" +
                "Email: " + application.getStudent().getEmail() + "\n" +
                "University: " + application.getStudent().getUniversity() + "\n" +
                "Major: " + application.getStudent().getMajor() + "\n\n" +
                "Please log in to your dashboard to review the application.\n\n" +
                "Best regards,\n" +
                "Student Internship Portal Team");

        emailSender.send(message);
    }

    public void sendApplicationStatusUpdateNotification(Application application) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(application.getStudent().getEmail());
        message.setSubject("Application Status Update - " + application.getInternshipPost().getTitle());
        message.setText("Dear " + application.getStudent().getFullName() + ",\n\n" +
                "Your application status for '" + application.getInternshipPost().getTitle() + "' at " +
                application.getInternshipPost().getCreatedBy().getCompanyName() + " has been updated.\n\n" +
                "New Status: " + application.getStatus() + "\n\n" +
                "Please log in to your dashboard for more details.\n\n" +
                "Best regards,\n" +
                "Student Internship Portal Team");

        emailSender.send(message);
    }
}
