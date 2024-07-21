package io.summary.jpa.jpa_summary.querydsl;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static io.summary.jpa.jpa_summary.entity.QBook.book;
import static io.summary.jpa.jpa_summary.entity.QPublisher.publisher;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.summary.jpa.jpa_summary.dto.BookDetailDto;
import io.summary.jpa.jpa_summary.dto.BookDto;
import io.summary.jpa.jpa_summary.dto.PublisherDetailDto;
import io.summary.jpa.jpa_summary.dto.QBookDetailDto;
import io.summary.jpa.jpa_summary.dto.QBookDto;
import io.summary.jpa.jpa_summary.dto.QPublisherDetailDto;
import io.summary.jpa.jpa_summary.entity.Publisher;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PublisherQuerydsl {
  private final JPAQueryFactory jpaQueryFactory;

  public List<Publisher> findAllPublisherWithBookLazy(){
    return jpaQueryFactory
        .selectFrom(publisher)
        .join(publisher.books, book)
        .fetch();
  }

  public List<BookDetailDto> findAllBookDetailDtoWithPublisherLazy(){
    return jpaQueryFactory
        .select(
            new QBookDetailDto(
                publisher.name.as("publisherName"),
                publisher.id.as("publisherId"),
                book.name.as("bookName"),
                book.id.as("bookId")
            )
        )
        .from(publisher)
        .join(publisher.books, book)
        .fetch();
  }

  public List<BookDetailDto> findAllBookDetailDtoByFetchJoin(){
    return jpaQueryFactory
        .select(
          new QBookDetailDto(
              publisher.name.as("publisherName"),
              publisher.id.as("publisherId"),
              book.name.as("bookName"),
              book.id.as("bookId")
          )
        )
        .from(publisher)
        .join(publisher.books, book)
        .fetchJoin()
        .fetch();
  }


  public List<Publisher> findAllPublisherByFetchJoin(){
    return jpaQueryFactory
        .selectFrom(publisher)
        .join(publisher.books, book)
        .fetchJoin()
        .fetch();
  }

  public List<PublisherDetailDto> findAllBookDetailDtoByTransform() {
    return jpaQueryFactory
        .selectFrom(publisher)
        .join(publisher.books, book)
        .transform(groupBy(publisher.id).list(
            new QPublisherDetailDto(
                publisher.id, publisher.name,
                GroupBy.list(
                    new QBookDto(book.id, book.name)
                )
            )
        ));
  }
}
