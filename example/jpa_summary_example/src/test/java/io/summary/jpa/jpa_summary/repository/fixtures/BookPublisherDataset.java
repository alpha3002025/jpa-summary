package io.summary.jpa.jpa_summary.repository.fixtures;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Component
public class BookPublisherDataset {
  @Autowired
  private DataSource dataSource;
  public void fetchDataSql(){
    ClassPathResource resource = new ClassPathResource("dataset/book_publisher/data.sql");
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
    populator.execute(dataSource);
  }
}
