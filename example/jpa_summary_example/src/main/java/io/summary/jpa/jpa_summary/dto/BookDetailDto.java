package io.summary.jpa.jpa_summary.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookDetailDto {
  private String publisherName;
  private Long publisherId;
  private String bookName;
  private Long bookId;

  @QueryProjection
  public BookDetailDto(
    String publisherName,
    Long publisherId,
    String bookName,
    Long bookId
  ){
    this.publisherName = publisherName;
    this.publisherId = publisherId;
    this.bookName = bookName;
    this.bookId = bookId;
  }
}
