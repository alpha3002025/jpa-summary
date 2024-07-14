package io.summary.jpa.jpa_summary.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"mysql-test"})
@SpringBootTest
public class UserRepositoryTest {
  @Autowired
  private UserRepository userRepository;
}
