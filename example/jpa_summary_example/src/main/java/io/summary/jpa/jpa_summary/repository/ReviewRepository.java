package io.summary.jpa.jpa_summary.repository;

import io.summary.jpa.jpa_summary.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  // reviewId 를 이용해서 reviewUser 엔티티의 그래프를 들고온다.
  @Query(
      "select r "
      + "from Review r "
      + "join fetch r.reviewUser "
      + "where r.id = :reviewId "
  )
  Review findReviewByReviewId(@Param("reviewId") Long reviewId);

  @Query(
      "select r "
      + "from Review r "
      + "join fetch r.reviewUser " // reviewUser
      + "where r.book.id = :bookId"
  )
  List<Review> findReviewsByBookIdFetch(@Param("bookId") Long bookId);

  @Query(
      "select r "
      + "from Review r "
      + "where r.book.id = :bookId"
  )
  List<Review> findReviewsByBookId(@Param("bookId") Long bookId);

  @Query(
      "select r "
      + "from Review r "
      + "join fetch r.reviewUser " // reviewUser
      + "where r.reviewUser.user.id = :userId"
  )
  List<Review> findReviewByUserId(@Param("userId") Long userId);
}
