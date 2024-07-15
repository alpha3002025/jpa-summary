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
@Table(name = "book")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "ofAll")
public class Book extends Item{
  @Column(name = "author_name", columnDefinition = "VARCHAR(150)")
  private String authorName;

  @Builder(builderClassName = "WithItemBuilder", builderMethodName = "withItemBuilder")
  public Book(String authorName, Item item){
    super.setId(item.getId());
    super.setName(item.getName());
    super.setPrice(item.getPrice());
    this.authorName = authorName;
  }
}
