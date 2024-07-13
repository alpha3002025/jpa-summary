package io.summary.jpa.jpa_summary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailDto {
  private String publisherName;
  private Long publisherId;
  private String bookName;
  private Long bookId;
}
