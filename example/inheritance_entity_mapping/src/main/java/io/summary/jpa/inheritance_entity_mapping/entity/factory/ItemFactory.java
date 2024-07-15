package io.summary.jpa.inheritance_entity_mapping.entity.factory;

import io.summary.jpa.inheritance_entity_mapping.entity.Item;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ItemFactory {
  public Item newItem(String name, BigDecimal price){
    return Item.ofItem(null, name, price);
  }
}
