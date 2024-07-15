package io.summary.jpa.inheritance_entity_mapping.repository;

import io.summary.jpa.inheritance_entity_mapping.entity.Album;
import io.summary.jpa.inheritance_entity_mapping.entity.Book;
import io.summary.jpa.inheritance_entity_mapping.entity.Item;
import io.summary.jpa.inheritance_entity_mapping.entity.Movie;
import io.summary.jpa.inheritance_entity_mapping.entity.factory.ItemFactory;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"ddl-auto"})
@SpringBootTest
public class SampleInsertTest {
  @Autowired
  private AlbumRepository albumRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private MovieRepository movieRepository;
  @Autowired
  private ItemFactory itemFactory;

  @Test
  public void test__insert_sample_data(){
    Item albumItem = itemFactory.newItem("비틀즈 명작모음", BigDecimal.valueOf(15000));
    Album album = Album.withItemBuilder()
        .item(albumItem)
        .artist("비틀즈")
        .build();

    Item bookItem = itemFactory.newItem("유전자 지배사회", BigDecimal.valueOf(15750));
    Book book = Book.withItemBuilder()
        .item(bookItem)
        .authorName("최정균")
        .build();

    Item movieItem = itemFactory.newItem("엽기적인 그녀", BigDecimal.valueOf(8000));
    Movie movie = Movie.withItemBuilder()
        .item(movieItem)
        .actor("전지현")
        .director("곽재용")
        .build();

    albumRepository.save(album);
    bookRepository.save(book);
    movieRepository.save(movie);
  }
}
