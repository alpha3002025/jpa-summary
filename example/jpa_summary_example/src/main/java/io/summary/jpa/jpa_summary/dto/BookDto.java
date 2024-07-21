package io.summary.jpa.jpa_summary.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class BookDto {
  private Long id;
  private String name;

  @QueryProjection
  public BookDto(
      Long id,
      String name
  ){
    this.id = id;
    this.name = name;
  }
}
