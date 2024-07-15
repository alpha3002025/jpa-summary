package io.summary.jpa.inheritance_entity_mapping.repository;

import io.summary.jpa.inheritance_entity_mapping.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {

}
