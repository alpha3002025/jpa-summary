package io.summary.jpa.jpa_summary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "movie")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Movie extends Item{
  @Column(name = "director", columnDefinition = "VARCHAR(150)")
  private String director;
  @Column(name = "actor", columnDefinition = "VARCHAR(150)")
  private String actor;
}
