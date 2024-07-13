package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {
  @Query(
      "select b "
      + "from Book b "
      + "inner join fetch b.reviewList "
      + "where b.id = :id"
  )
  Book findBookByIdFetch(@Param("id") Long bookId);
}
