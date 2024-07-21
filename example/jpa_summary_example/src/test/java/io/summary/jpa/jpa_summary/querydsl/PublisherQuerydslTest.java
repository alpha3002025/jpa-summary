package io.summary.jpa.jpa_summary.querydsl;

import io.summary.jpa.jpa_summary.dto.BookDetailDto;
import io.summary.jpa.jpa_summary.dto.PublisherDetailDto;
import io.summary.jpa.jpa_summary.entity.Publisher;
import io.summary.jpa.jpa_summary.querydsl.fixtures.PublisherBookQuerydslDataset1;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
class PublisherQuerydslTest {
  @Autowired
  private PublisherQuerydsl publisherQuerydsl;
  @Autowired
  private PublisherBookQuerydslDataset1 publisherBookQuerydslDataset1;

  private final Logger logger = LoggerFactory.getLogger(PublisherQuerydslTest.class);

  @BeforeEach
  public void init(){
    publisherBookQuerydslDataset1.init();
  }

  @AfterEach
  public void destroy(){
    publisherBookQuerydslDataset1.truncate();
  }

  // 엔티티 그래프 탐색시 n+1 이 발생하면서 Session 이 없다는 에러가 뜬다.
  // 따라서 Transaction 세션을 타도록 @Transactional 을 추가해줬다.
  @Transactional
  @Test
  public void 테스트_일반_Join___Publisher_엔티티_조회(){
    List<Publisher> publishers = publisherQuerydsl.findAllPublisherWithBookLazy();
    // sql 콘솔에는 join 으로 조회해오는 것 같지만 조인을 하지 않는다. 엔티티 그래프가 비어있는 채로 채워진다.
    logger.info("(1) publishers = {} ", publishers);

    // n+1 발생 (일반 조인을 하게 되면 결과값의 엔티티 그래프는 비어있는 채로 채운다.)
    publishers.stream()
        .forEach(p -> {
          logger.info("(2) publisher name = {}, books = {}", p.getName(), p.getBooks());
        });
  }

  @Test
  public void 테스트_일반_Join___Publisher_Dto_조회(){
    List<BookDetailDto> list = publisherQuerydsl.findAllBookDetailDtoWithPublisherLazy();
    logger.info("list = {}", list);
    // 특이하다. Dto 조회는 FetchType.Lazy 여도 join 후 Dto에 데이터 프로젝션 하면서  값들이 모두 바인딩 된다.
    // Dto 를 사용해 Projection 할 경우에는 FetchType.Lazy 이더라도 fetch join 을 한다.
  }

  @Test
  public void 테스트_fetch_Join__Publisher를_Entity로_조회(){
    List<Publisher> publisherList = publisherQuerydsl.findAllPublisherByFetchJoin();
    publisherList.stream()
        .forEach(p -> {
          logger.info(">>> publisherName = {}", p.getName());
          p.getBooks().stream()
              .forEach(b -> {
                logger.info("bookName = {}", b.getName());
              });
        });
  }

  @Test
  public void 테스트_fetch_Join___Publisher를_Dto로_조회(){
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          List<BookDetailDto> bookList = publisherQuerydsl.findAllBookDetailDtoByFetchJoin();

          bookList.stream()
              .forEach(b -> {
                logger.info("book = {}", b);
              });
        }
    );
  }

  @Transactional
  @Test
  public void 테스트_transform_메서드로_Publisher_Book_엔티티를_Dto로_조회(){
    List<PublisherDetailDto> publisherList = publisherQuerydsl.findAllBookDetailDtoByTransform();
    logger.info("publisherList = {}", publisherList);
  }


}