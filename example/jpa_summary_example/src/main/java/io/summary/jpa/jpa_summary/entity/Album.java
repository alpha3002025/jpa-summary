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
@Table(name = "album")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Album extends Item{
  @Column(name = "artist", columnDefinition = "VARCHAR(150)")
  private String artist;
}
