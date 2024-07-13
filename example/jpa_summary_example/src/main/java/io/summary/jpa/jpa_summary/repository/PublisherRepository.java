package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
  @Query(
      "select p "
    + "from Publisher p "
    + "inner join fetch p.books "
    + "where p.id = :id"
  )
  Publisher findBookByIdFetch(@Param("id") Long id);
}
