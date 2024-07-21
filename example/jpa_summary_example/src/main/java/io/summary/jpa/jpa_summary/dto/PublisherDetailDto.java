package io.summary.jpa.jpa_summary.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class PublisherDetailDto {
  private Long publisherId;
  private String publisherName;
  private List<BookDto> books;

  @QueryProjection
  public PublisherDetailDto(
      Long publisherId,
      String publisherName,
      List<BookDto> books
  ){
      this.publisherId = publisherId;
      this.publisherName = publisherName;
      this.books = books;
  }
}
