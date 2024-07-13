package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.dto.BookDetailDto;
import io.summary.jpa.jpa_summary.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@Profile("mysql")
@SpringBootTest
public class BookRepositoryTest {
  @Autowired
  private BookRepository bookRepository;

  @Test
  public void test__book_조회_1(){
    Book book = bookRepository
        .findById(1L)
        .orElseThrow(() -> new RuntimeException("데이터가 존재하지 않습니다."));
  }

  @Test
  public void test__출판사정보와_함께_데이터조회(){
    BookDetailDto bookDetailDto = bookRepository
        .findByIdReturnBookDetailDto(1L);
  }
}
