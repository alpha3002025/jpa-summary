package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.repository.fixtures.ReviewUserDataset;
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
public class ReviewUserRepositoryTest {
  @Autowired
  private ReviewUserRepository reviewUserRepository;
  @Autowired
  private ReviewUserDataset dataset;

  private static final Logger logger = LoggerFactory.getLogger(ReviewUserRepositoryTest.class);

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
  public void test__리뷰정보와_함께_사용자정보를_조회(){

  }

}
