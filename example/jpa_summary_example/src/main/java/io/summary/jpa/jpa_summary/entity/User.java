package io.summary.jpa.jpa_summary.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;

  @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  private List<ReviewUser> reviewUserList = new ArrayList<>();

  @Column(name = "email", columnDefinition = "VARCHAR(300)")
  private String email;

  @Column(name = "name", columnDefinition = "VARCHAR(100)")
  private String name;
}
