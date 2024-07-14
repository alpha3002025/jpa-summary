package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.entity.Book;
import io.summary.jpa.jpa_summary.entity.Review;
import io.summary.jpa.jpa_summary.entity.ReviewUser;
import io.summary.jpa.jpa_summary.entity.User;
import io.summary.jpa.jpa_summary.repository.fixtures.ReviewUserDataset;
import java.util.List;
import java.util.Optional;
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
public class ReviewRepositoryTest {
  @Autowired
  private ReviewRepository reviewRepository;
  @Autowired
  private ReviewUserDataset dataset;
  private static final Logger logger = LoggerFactory.getLogger(ReviewRepositoryTest.class);

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
  public void test__리뷰정보_한건에_대한_ReviewUser_정보를_조회(){
    Review review = Optional
        .ofNullable(reviewRepository.findReviewByReviewId(1L))
        .orElseThrow(() -> new RuntimeException("일치하는 리뷰가 없습니다."));

    final Book book = review.getBook();
    ReviewUser reviewUser = review.getReviewUser();

    logger.info("bookId = {}, bookName = {}, userId = {}",
        book.getId(),
        book.getName(),
        reviewUser.getUser().getId()
    );
  }

  @Transactional
  @Test
  public void test__책_하나에_대한_리뷰들_조회(){
    // bookRepository 에도 따로 정의한 쿼리 메서드
    // bookRepository 에서 수행하는 것이 더 효율적이다.
    List<Review> reviews = reviewRepository.findReviewsByBookIdFetch(1L);
    reviews.stream()
        .forEach(review -> {
          Book book = review.getBook();
          User user = review.getReviewUser().getUser();
          logger.info("bookId = {}, bookName = {}, userId = {}",
              book.getId(),
              book.getName(),
              user.getName()
          );
        });
  }

  @Transactional
  @Test
  public void test__사용자id에_대한_리뷰들_조회(){
    List<Review> reviews = reviewRepository.findReviewByUserId(1L);

    reviews.stream()
        .forEach(review -> {
          Book book = review.getBook();
          logger.info("bookId = {}, bookName = {}, userId = {}, username = {}",
              book.getId(),
              book.getName(),
              review.getReviewUser().getUser().getId(),
              review.getReviewUser().getUser().getName()
          );
        });
  }

}
