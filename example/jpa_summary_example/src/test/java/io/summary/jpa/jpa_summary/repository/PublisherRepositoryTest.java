package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.config.extension.DatabaseClearExtension;
import io.summary.jpa.jpa_summary.entity.Publisher;
import io.summary.jpa.jpa_summary.repository.fixtures.BookPublisherDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles({"mysql-test"})
@SpringBootTest
public class PublisherRepositoryTest {
  @Autowired
  private PublisherRepository publisherRepository;
  @Autowired
  private BookPublisherDataset bookPublisherDataset;

  private static final Logger logger = LoggerFactory.getLogger(PublisherRepositoryTest.class);

  @BeforeEach
  public void init(){
    bookPublisherDataset.fetchDataSql();
  }

  @Transactional
  @Test
  public void test__책정보와_함께_출판사정보를_조회(){
    Publisher publisher = publisherRepository.findBookDetailDtoById(1L);
    logger.info("publisher == {}", publisher.toString());
    publisher.getBooks().forEach(book -> logger.info("book.name = {}", book.getName()));
  }
}
