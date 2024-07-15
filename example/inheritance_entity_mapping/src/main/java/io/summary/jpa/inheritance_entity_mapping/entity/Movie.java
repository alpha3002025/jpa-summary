package io.summary.jpa.inheritance_entity_mapping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "movie")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "ofAll")
public class Movie extends Item{
  @Column(name = "director", columnDefinition = "VARCHAR(150)")
  private String director;
  @Column(name = "actor", columnDefinition = "VARCHAR(150)")
  private String actor;

  @Builder(builderClassName = "WithItemBuilder", builderMethodName = "withItemBuilder")
  public Movie(String director, String actor, Item item){
    super.setId(item.getId());
    super.setName(item.getName());
    super.setPrice(item.getPrice());
    this.director = director;
    this.actor = actor;
  }
}
