package com.internship.portal.repository;

import com.internship.portal.model.InternshipPost;
import com.internship.portal.model.PostStatus;
import com.internship.portal.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InternshipPostRepository extends JpaRepository<InternshipPost, Long> {
    List<InternshipPost> findByCreatedBy(User createdBy);
    Page<InternshipPost> findByCreatedBy(User createdBy, Pageable pageable);

    List<InternshipPost> findByStatus(PostStatus status);
    Page<InternshipPost> findByStatus(PostStatus status, Pageable pageable);

    @Query("SELECT i FROM InternshipPost i WHERE i.status = :status AND " +
            "(LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.location) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<InternshipPost> findByStatusAndSearch(@Param("status") PostStatus status, @Param("search") String search, Pageable pageable);

    @Query("SELECT i FROM InternshipPost i WHERE i.status = :status AND " +
            "LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<InternshipPost> findByStatusAndLocation(@Param("status") PostStatus status, @Param("location") String location, Pageable pageable);

    @Query("SELECT i FROM InternshipPost i WHERE i.status = :status AND " +
            "(LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<InternshipPost> findByStatusAndSearchAndLocation(@Param("status") PostStatus status, @Param("search") String search, @Param("location") String location, Pageable pageable);

    List<InternshipPost> findByDeadlineBefore(LocalDateTime deadline);

    long countByStatus(PostStatus status);
    long countByCreatedBy(User createdBy);
}
