package com.internship.portal.repository;

import com.internship.portal.model.InternshipPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipPostRepository extends JpaRepository<InternshipPost, Long> {

}
