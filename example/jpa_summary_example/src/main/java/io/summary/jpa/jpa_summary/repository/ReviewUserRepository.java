package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.entity.ReviewUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewUserRepository extends JpaRepository<ReviewUser, Long> {
}
