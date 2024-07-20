package io.summary.jpa.jpa_summary.querydsl.fixtures;


import jakarta.persistence.EntityManager;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile({"mysql-test"})
@Component
public class PublisherBookQuerydslDataset1 {
  private final DataSource dataSource;
  private final EntityManager entityManager;

  public PublisherBookQuerydslDataset1(
      DataSource dataSource,
      EntityManager entityManager
  ){
    this.dataSource = dataSource;
    this.entityManager = entityManager;
  }

  @Transactional
  public void truncate(){
    entityManager.clear();

    List<String> tables = entityManager
        .createNativeQuery("SHOW TABLES")
        .getResultList();

    tables.stream()
        .forEach(table -> entityManager
            .createNativeQuery(String.format("TRUNCATE TABLE %s", table))
            .executeUpdate()
        );
  }

  @Transactional
  public void fetchDataSql(){
    ClassPathResource resource = new ClassPathResource("dataset/3_querydsl_review_user_fetch_join/data.sql");
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
    populator.execute(dataSource);
  }


}
