package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.entity.Book;
import io.summary.jpa.jpa_summary.repository.fixtures.ReviewBookDataset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles({"mysql-test"})
@SpringBootTest
public class BookRepositoryTest {
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private ReviewBookDataset dataset;

  private static final Logger logger = LoggerFactory.getLogger(BookRepositoryTest.class);

  @BeforeEach
  public void init(){
    dataset.init();
  }

  @AfterEach
  public void destroy(){
    dataset.truncate();
  }

  @Transactional
  @Test
  public void test__책정보와_함께_리뷰정보를_조회(){
    Book book = bookRepository.findBookByIdFetch(1L);
    logger.info("book == {}", book.toString());
    book.getReviewList().forEach(review -> logger.info("review.title = {}, review.contents = {}", review.getTitle(), review.getContents()));
  }
}
