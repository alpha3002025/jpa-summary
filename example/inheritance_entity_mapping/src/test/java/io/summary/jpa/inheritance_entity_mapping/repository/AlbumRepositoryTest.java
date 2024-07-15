package io.summary.jpa.inheritance_entity_mapping.repository;

import io.summary.jpa.inheritance_entity_mapping.entity.Album;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"ddl-auto"})
@SpringBootTest
class AlbumRepositoryTest {
  @Autowired
  private AlbumRepository albumRepository;

  @Test
  public void test__album_save(){
    Album album = Album.ofAll("비틀즈 명곡모음");
    albumRepository.save(album);
  }
}