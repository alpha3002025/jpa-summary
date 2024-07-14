package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
