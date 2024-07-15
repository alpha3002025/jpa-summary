package io.summary.jpa.inheritance_entity_mapping.repository;

import io.summary.jpa.inheritance_entity_mapping.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

}
