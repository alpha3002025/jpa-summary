package io.summary.jpa.jpa_summary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "book")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "ofAll")
@EqualsAndHashCode(exclude = {"publisher", "reviewList"})
public class Book extends Item {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "publisher_id", columnDefinition = "BIGINT")
  @ToString.Exclude
  private Publisher publisher;

  @OneToMany(mappedBy = "book")
  @ToString.Exclude
  private List<Review> reviewList = new ArrayList<>();
}
