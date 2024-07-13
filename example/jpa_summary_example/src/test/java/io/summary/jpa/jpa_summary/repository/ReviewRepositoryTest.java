package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.config.extension.DatabaseClearExtension;
import io.summary.jpa.jpa_summary.repository.fixtures.ReviewBookDataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(DatabaseClearExtension.class)
@ActiveProfiles({"mysql-test"})
@SpringBootTest
public class ReviewRepositoryTest {
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private ReviewBookDataset dataset;
  private static final Logger logger = LoggerFactory.getLogger(ReviewRepositoryTest.class);

  @BeforeEach
  public void init(){
    dataset.fetchDataSql();
  }
}
