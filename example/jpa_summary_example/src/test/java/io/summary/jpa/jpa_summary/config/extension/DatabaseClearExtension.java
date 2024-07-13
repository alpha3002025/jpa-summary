package io.summary.jpa.jpa_summary.config.extension;

import io.summary.jpa.jpa_summary.config.DatabaseCleaner;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DatabaseClearExtension implements BeforeEachCallback {

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    DatabaseCleaner cleaner = getDatabaseCleaner(context);
    cleaner.removeAll();
  }

  private DatabaseCleaner getDatabaseCleaner(ExtensionContext extensionContext){
    return SpringExtension
        .getApplicationContext(extensionContext)
        .getBean(DatabaseCleaner.class);
  }
}
