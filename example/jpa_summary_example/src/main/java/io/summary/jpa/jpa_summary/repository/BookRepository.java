package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.dto.BookDetailDto;
import io.summary.jpa.jpa_summary.entity.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

  @Query(
      "select new io.summary.jpa.jpa_summary.dto.BookDetailDto("
          + " b.publisher.name, b.publisher.id, b.name, b.id"
          + ") "
      + "from Book b "
      + "inner join b.publisher ")
  BookDetailDto findByIdReturnBookDetailDto(Long id);
}
