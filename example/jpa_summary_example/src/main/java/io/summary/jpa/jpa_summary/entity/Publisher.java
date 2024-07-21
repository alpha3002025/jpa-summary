package io.summary.jpa.jpa_summary.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "publisher")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Publisher {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "publisher", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<Book> books = new ArrayList<>();

  @Column(name = "name", columnDefinition = "VARCHAR(70)")
  private String name;
}
