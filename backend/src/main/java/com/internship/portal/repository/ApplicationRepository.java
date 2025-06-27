package com.internship.portal.repository;

import com.internship.portal.model.Application;
import com.internship.portal.model.ApplicationStatus;
import com.internship.portal.model.InternshipPost;
import com.internship.portal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudent(User student);
    Page<Application> findByStudent(User student, Pageable pageable);

    List<Application> findByInternshipPost(InternshipPost internshipPost);
    Page<Application> findByInternshipPost(InternshipPost internshipPost, Pageable pageable);

    List<Application> findByStatus(ApplicationStatus status);
    Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

    Optional<Application> findByStudentAndInternshipPost(User student, InternshipPost internshipPost);

    boolean existsByStudentAndInternshipPost(User student, InternshipPost internshipPost);

    @Query("SELECT a FROM Application a WHERE a.internshipPost.createdBy = :company")
    List<Application> findByCompany(@Param("company") User company);

    @Query("SELECT a FROM Application a WHERE a.internshipPost.createdBy = :company")
    Page<Application> findByCompany(@Param("company") User company, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.internshipPost.createdBy = :company AND a.status = :status")
    Page<Application> findByCompanyAndStatus(@Param("company") User company, @Param("status") ApplicationStatus status, Pageable pageable);

    long countByInternshipPost(InternshipPost internshipPost);
    long countByStudent(User student);
    long countByStatus(ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.internshipPost.createdBy = :company")
    long countByCompany(@Param("company") User company);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.internshipPost.createdBy = :company AND a.status = :status")
    long countByCompanyAndStatus(@Param("company") User company, @Param("status") ApplicationStatus status);
}
