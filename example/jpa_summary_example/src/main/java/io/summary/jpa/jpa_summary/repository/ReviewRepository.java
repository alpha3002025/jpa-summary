package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
