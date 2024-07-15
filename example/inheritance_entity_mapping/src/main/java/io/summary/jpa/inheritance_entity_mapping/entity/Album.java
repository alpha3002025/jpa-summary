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
@Table(name = "album")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "ofAll")
public class Album extends Item{
  @Column(name = "artist", columnDefinition = "VARCHAR(150)")
  private String artist;

  @Builder(builderClassName = "WithItemBuilder", builderMethodName = "withItemBuilder")
  public Album(String artist, Item item){
    super.setId(item.getId());
    super.setName(item.getName());
    super.setPrice(item.getPrice());
    this.artist = artist;
  }
}
