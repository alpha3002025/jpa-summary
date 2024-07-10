# 영속성 전이 (Cascade), 고아 제거 (orphan removal) 속성

**영속성 전이 (Cascade)**<br/>

Cascade 의 영어 단어 의미는 "작은 폭포"입니다. 영속성에 있어서 Cascade 의 의미는 다음과 같습니다.<br/>
- "폭포가 위에서 물을 받으면 아래로 흘려보내듯 영속성도 하나의 객체에서 다른 객체로 영속성을 흘려보내준다는 의미"

<br/>

OneToOne, OneToMany, ManyToMany, ManyToOne 등으로 정의된 객체에서 모두 cascade 를 사용가능합니다.<br/>

연관관계의 다른 객체들의 저장/삭제/수정을 직접 Repository 의 메서드를 직접 호출해서 저장하는 것은 다소 관계형 데이터베이스 같은 느낌이 있습니다.<br/>

Cascade 의 속성을 잘 활용하면 엔티티의 변경이 발생시 연관관계에 해당하는 엔티티에게도 전이를 일으켜서 저장/삭제/수정 구문을 하드코딩하지 않고도 엔티티 매니저가 저장/삭제/수정을 수행하도록  할 수 있습니다.<br/>

어떤 영속성 객체에 대한 연관관계의 객체에 대해 실무에서는 직접 그 객체의 Repository 에 접근해서 save(), remove() 하기보다는 Cascade.PERSIST, Cascade.REMOVE 등을 이용하는 경우가 많습니다.<br/>

<br/>

CascadeType 은 다음과 같은 종류들이 있습니다.

- PERSIST : book 이 PERSIST 를 하는 순간에 연관관계에 있는 Publisher 도 Persist 를 합니다.
- MERGE : book 이 MERGE(update 쿼리) 가 일어나는 순간에 연관관계에 있는 Publisher 도 Merge 를 합니다.
- DETACH : book 이 DETACH 를 하는 순간에 연관관계에 있는 Publisher 도 DETACH 를 합니다..
- REFRESH : book 엔티티를 다시 로딩을 했을 때 연관관계에 있는 Publisher 도 Refresh 를 합니다..
- ALL : 모든 경우에 대해 영속성 전이를 하도록 지정합니다.
- REMOVE : cascade 를 사용할 때 많이 사용하는 옵션이기도 하지만, 실수했을때 잘못될 가능성이 크기 때문에 주의가 필요한 옵션입니다.
  - 현업에서는 주로 Remove 를 바로 하기보다는 해당 row 를 delete = true 마스킹을 하는 soft delete 방식을 채택합니다.
  - 여기에 대해서는 이 글의 후반부에 따로 정리합니다.

<br/>



**orphanRemoval**<br/>

orphanRemoval 은 연관관계가 없는 고아 객체를 삭제하는 것을 의미합니다. 만약 어떤 엔티티 내의 연관관계의 필드를 null 로 지정했을 때 그 연관관계 필드가 삭제되게끔 하고 싶다면 `orphanRemoval = true` 를 상대편 객체에 지정해주면 됩니다. 만약 null 로 지정한 연관관계 필드가 삭제되지 않기를 원한다면 `orphanRemoval = false` 로 지정해주면 됩니다.<br/>

고아제거 속성이라고 불리는 orphanRemoval 은 신중히 사용해야 합니다. 항상 필요한 개념이 아니기에 신중하게 적용해야 합니다. 만약 Comment 라는 엔티티내에 commentHistory 를 null 로 추가했을 때 댓글 기록까지 모두 지우는 것이 컨텐츠 정책에 위배된다면 이런 경우에는 orphanRemoval 은 필요하지 않습니다.<br/>

<br/>



**Soft Delete**<br/>

실제 B2C 기업들에서는 사용자의 댓글, 글 등과 같은 정보를 바로 삭제하지 않고 삭제 마스킹 처리를 한후 개인 정보 보관 연한(연도한기)가 지날 경우 배치를 통해 삭제처리를 합니다. 이렇게 하는 이유는 범죄 발생시 조사기관에서 기록을 요구하게 되기 때문입니다.<br/>

<br/>



## e.g. `@OneToMany` 의 cascade 속성

`@OneToMany` 의 cascade 속성은 다음과 같습니다.

```java
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface OneToMany {
    Class targetEntity() default void.class;

    /** (Optional) ... */
    CascadeType[] cascade() default {}; // cascade 

    /** (Optional) ... */
    FetchType fetch() default LAZY;

    String mappedBy() default "";

    /** (Optional) ... */
    boolean orphanRemoval() default false;
}
```

<br/>



CascadeType 은 다음과 같습니다.

```java
public enum CascadeType { 

    /** Cascade all operations */
    ALL, 

    /** Cascade persist operation */
    PERSIST, 

    /** Cascade merge operation */
    MERGE, 

    /** Cascade remove operation */
    REMOVE,

    /** Cascade refresh operation */
    REFRESH,

    /**
     * Cascade detach operation
     *
     * @since 2.0
     * 
     */   
    DETACH
}
```

<br/>



## 연관관계의 객체를 저장/수정/삭제 하지 않고 트랜잭션을 종료할 때

아래와 같은 테스트 코드가 있다고 해보겠습니다.

```java
@SpringBootTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void bookCascadeTest() {
        Book book = new Book();
        book.setName("A Book on C");

        Publisher publisher = new Publisher();
        publisher.setName("Developer Publishing");

        book.setPublisher(publisher);
        bookRepository.save(book);

        // 실제 코드 작성 없이 영속성 전이로 이뤄지는지를 테스트할 것이기에 publisher 측의 코드들은 주석처리
        // publisher.addBook(book);
        // publisherRepository.save(publisher);


        System.out.println("books : " + bookRepository.findAll());
        System.out.println("publishers : " + publisherRepository.findAll());
    }
}
```

<br/>



publisher 엔티티를 생성해두고 저장하지 않으면서 코드를 저장합니다.<br/>

이 코드는 에러가 납니다.<br/>

```
object references an unsaved transient instance - save the transient instance before ...
```

"Object 가 저장이 되지 않은 transient instance 를 가리키고 있다" 라고 설명합니다.<br/>

<br/>



이 문구를 조금 더 자세히 설명해보면 다음과 같습니다.

- 엔티티의 Relation 레퍼런스가 DB에 저장되지 않고 그냥 자바객체이기 때문에 저장할 수 없다.



Book 이 '관리' 상태로 진입되지 않은 상태에서 연관관계의 Publisher 객체들 역시 '관리'상태로 진입되는데 이 상태에서 `bookRepository.save()` 를 하고 있습니다. `bookRepository.save()` 시에 연관관계의 객체인 Publisher 객체들이 '관리' 상태가 아니기 때문에 에러가 발생하는 것입니다.<br/>



이 경우 해결책은 두가지입니다.

- cascade 옵션을 Book 엔티티 내의 publisher 에 지정해줍니다.
- publisherRepository.save() 를 Book 엔티티 save() 직전에 호출하도록 정의해줍니다.
  - 이 방식은 기존의 SQL 기반의 Data 접근 로직 처리 방식과 다른 방식이 아니기에 JPA 진영에서는 추천되지 않는 방식입니다.

 <br/>



## CascadeType.PERSIST

cascade 옵션을 `CascadeType.PERSIST` 으로 수정 

```java
@Entity
// ...
public class Book extends BaseEntity {
    
    // ...

    @ManyToOne(cascade = { CascadeType.PERSIST })
    @ToString.Exclude
    private Publisher publisher;

    // ...
}
```

<br/>



## CascadeType.MERGE

만약 insert 쿼리가 아닌 update 쿼리 역시도 Transaction 종료시 수행되기를 원한다면 Book 엔티티를 아래와 같이 수정해줍니다.

cascade 옵션으로 `CasecadeType.MERGE` 추가

```java
@Entity
// ...
public class Book extends BaseEntity {
    
    // ...

    // CascadeType.MERGE 를 추가해줬다. 
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @ToString.Exclude
    private Publisher publisher;

    // ...
}
```

- 만약 `CascadeType.MERGE` 을 추가해주지 않는다면 update 쿼리는 발생하지 않게 됩니다.

<br/>



## Cascade.REMOVE

CascadeType.REMOVE 를 사용하면 객체 삭제시 연관관계에 대한 필드도 함께 삭제합니다.<br/>

하지만 CascadeType.REMOVE 만 사용하면 연관관계 필드를 null 로 지정할 때 삭제는 되지 않습니다. 
만약 연관관계 필드를 null 로 지정했을 때 그 연관관계 필드가 삭제되게끔 하고 싶다면 orphanRemoval 을 상대편 객체에 지정해주면 됩니다. 

참고로 cascade 는 아래와 같이 `default = {}` 로 선언되어 있기 때문에 연관관계 필드에 cascade 속성을 지정하지 않으면 영속성전이가 일어나지 않습니다.

<br/>



### e.g. CascadeType.REMOVE

Book.java

```java
// ...
@Entity
public class Book extends BaseEntity {
    
    // ...

    // CascadeType.REMOVE 를 추가해줬다. 
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @ToString.Exclude
    private Publisher publisher;

    // ...
}
```

<br/>



테스트코드

```java
@SpringBootTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void bookRemoveByCascadeTest() {
        bookRepository.deleteById(1L);

        System.out.println("books : " + bookRepository.findAll());
        System.out.println("publishers : " + publisherRepository.findAll());

        bookRepository.findAll().forEach(book -> System.out.println(book.getPublisher()));
    }
}
```

<br/>

샘플데이터를 book(id=1, publisher_id=1, ...), book(id=2, publisher_id=1, ...) 으로 데이터를 입력해둔 후에 
실행결과를 보면 다음과 같이 수행됩니다.

- book id=1 인 book 에 대해서는 delete from book where id=? , delete from publisher where id=? 이 호출됩니다.
- book id=2 인 book 에 대해서는 update book set publisher_id=null where publisher_id=? 이 호출됩니다.

<br/>



### e.g. CascadeType.REMOVE 없이 삭제 수행

BookEntity

```java
// ...
@Entity
public class Book extends BaseEntity {
    
    // ...

    // CascadeType.REMOVE 를 추가해줬다. 
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    private Publisher publisher;

    // ...
}
```

<br/>



테스트 코드

```java
@SpringBootTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void bookCascadeTest() {
        Book book = new Book();
        book.setName("다이어트 혁명");

        Publisher publisher = new Publisher();
        publisher.setName("21세기 북스");

        book.setPublisher(publisher);
        bookRepository.save(book);

        // 실제 코드 작성 없이 영속성 전이로 이뤄지는지를 테스트할 것이기에 publisher 측의 코드들은 주석처리
        // publisher.addBook(book);
        // publisherRepository.save(publisher);


        System.out.println("books : " + bookRepository.findAll());
        System.out.println("publishers : " + publisherRepository.findAll());

        Book book1 = bookRepository.findById(1L).get();
        book1.getPublisher().setName("만칼로리 혁명");

        bookRepository.save(book1);
        System.out.println("publishers : " + publisherRepository.findAll());

        // 새로 추가해준 부분 //////////////
        Book book2 = bookRepository.findById(1L).get();
        bookRepository.delete(book2); // (1)

        System.out.println("books : " + bookRepository.findAll()); // (2)
        System.out.println("publishers : " + publisherRepository.findAll()); // (2)
    }
}
```

(1)

- bookRepository.delete(book2) 를 할때 book 에 연관된 publisher 에는 아무 동작이 일어나지 않습니다.

(2)

- 데이터를 확인해보면 book 데이터가 없더라도 publisher 는 존재하는 버그가 발생하게 됩니다.

<br/>



## cascade - 이 외의 다른 CascadeType 들

- DETACH : book 이 DETACH 를 하는 순간에 연관관계에 있는 Publisher 도 DETACH 를 합니다..
- REFRESH : book 엔티티를 다시 로딩을 했을 때 연관관계에 있는 Publisher 도 Refresh 를 합니다..
- ALL : 모든 경우에 대해 영속성 전이를 하도록 지정합니다.
- REMOVE : cascade 를 사용할 때 많이 사용하는 옵션이기도 하지만, 실수했을때 복원하지 못할 가능성이 크기 때문에 주의가 필요한 옵션입니다.
  - 현업에서는 주로 Remove 를 바로 하기보다는 해당 row 를 delete = true 마스킹을 하는 soft delete 방식을 채택합니다.
  - 여기에 대해서는 이 글의 후반부에 따로 정리합니다.

<br/>



## 고아 제거 속성 (orphanRemoval)

CascadeType.REMOVE 를 사용하면 객체 삭제시 연관관계에 대한 필드도 함께 삭제합니다.<br/>

하지만 CascadeType.REMOVE 만 사용하면 연관관계 필드를 null 로 지정할 때 삭제는 되지 않습니다. 
만약 연관관계 필드를 null 로 지정했을 때 그 연관관계 필드가 삭제되게끔 하고 싶다면 orphanRemoval 을 상대편 객체에 지정해주면 됩니다. <br/>

orphanRemoval 은 연관관계가 없는 고아 객체를 삭제하는 것을 의미합니다. 만약 어떤 엔티티 내의 연관관계의 필드를 null 로 지정했을 때 그 연관관계 필드가 삭제되게끔 하고 싶다면 `orphanRemoval = true` 를 상대편 객체에 지정해주면 됩니다. 만약 null 로 지정한 연관관계 필드가 삭제되지 않기를 원한다면 `orphanRemoval = false` 로 지정해주면 됩니다.<br/>

고아제거 속성이라고 불리는 orphanRemoval 은 신중히 사용해야 합니다. 항상 필요한 개념이 아니기에 신중하게 적용해야 합니다. 만약 Comment 라는 엔티티내에 commentHistory 를 null 로 추가했을 때 댓글 기록까지 모두 지우는 것이 컨텐츠 정책에 위배된다면 이런 경우에는 orphanRemoval 은 필요하지 않습니다.<br/>

스프링 공식 문서에서는 아래와 같이 이야기하고 있습니다.

- For orphan removal: If you invoke setOrders(null), the related Order entities will be removed in db automatically.

- For remove cascade: If you invoke setOrders(null), the related Order entities will NOT be removed in db automatically.



이전 챕터에서 살펴본 book(id=1) 인 엔티티를 삭제할 때에는 cascade 처리를 했습니다. 이번에는 삭제하는 것이 아니라 book(id=1, publisher\_id = 1) 인 객체에 대해 publisher 를 null 로 정의할 경우 해당 publisher 도 삭제되게끔 하는 처리를 확인해봅니다.<br/>



```java
@SpringBootTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void bookOrphanRemovalTest() {
        Book book = new Book();
        book.setName("글루코스 혁명");

        Publisher publisher = new Publisher();
        publisher.setName("아침사과");

        book.setPublisher(publisher);
        bookRepository.save(book);

        // 실제 코드 작성 없이 영속성 전이로 이뤄지는지를 테스트할 것이기에 publisher 측의 코드들은 주석처리
        // publisher.addBook(book);
        // publisherRepository.save(publisher);


        System.out.println("books : " + bookRepository.findAll());
        System.out.println("publishers : " + publisherRepository.findAll());

        Book book1 = bookRepository.findById(1L).get();
        book1.getPublisher().setName("탄수화물 혁명");

        bookRepository.save(book1);
        System.out.println("publishers : " + publisherRepository.findAll());

        // Book book2 = bookRepository.findById(1L).get(); // 주석처리 
        // bookRepository.delete(book2); // 주석처리 

        // 새로 추가해준 부분 //////////////
        Book book3 = bookRepository.findById(1L).get();
        book3.setPublisher(null); // 연관관계 제거 
        // //////////////

        bookRepository.save(book3);

        System.out.println("books : " + bookRepository.findAll());
        System.out.println("publishers : " + publisherRepository.findAll());
        // 새로 추가해준 부분 //////////////
        System.out.println("book3-publisher : " + bookRepository.findById(1L).get().getPublisher());
        // //////////////
    }
}
```

<br/>



출력결과

```
publishers : [Publisher(super = ..., id=1, name=슬로우캠퍼스)]
... 
book3-publisher : null
```

<br/>



만약 고아객체 역시 삭제를 해주려면 아래와 같이 연관관계의 상대편인 Publisher 애 orphanRemoval = true 를 지정해줍니다.

```java
@Entity
public class Publisher extends BaseEntity{
    
    // ...

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "publisher_id")
    @ToString.Exclude
    private List<Book> books = new ArrayList<>();

    // ...

}
```

<br/>



## Soft Delete

이번에 살펴볼 내용은 orphanRemoval, cascade 를 사용하는 것 말고, 데이터 삭제시 물리적인 삭제가 아니라 삭제 마스킹하는 처리 방식입니다. 물리적으로 데이터를 바로 삭제할 경우 데이터를 롤백시키기도 쉽지 않고 이력보관 역시 쉽지 않습니다. <br/>

실제 B2C 기업들에서는 사용자의 댓글, 글 등과 같은 정보를 바로 삭제하지 않고 삭제 마스킹 처리를 한후 개인 정보 보관 연한(연도한기)가 지날 경우 배치를 통해 삭제처리를 합니다. 이렇게 하는 이유는 범죄 발생시 조사기관에서 기록을 요구하게 되기 때문입니다.<br/>

```java
@Entity
public class Book extends BaseEntity{

    // ...
    
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @ToString.Exclude
    private Publisher publisher;

    // deleted 필드를 추가해줬다. 참고로 테이블에 `deleted` 컬럼 역시 추가해줘야 한다. 
    private boolean deleted;
}
```

<br/>



리포지터리에서 삭제 마스킹된 필드를 조회할 때는 아래와 같이 deletedFalse 등과 같은 쿼리 메서드로 조회한다.

```java
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByDeletedFalse();
    List<Book> findByCategoryIsNullAndDeletedFalse();
}
```

<br/>



테스트 코드

```java
@SpringBootTest
public class BookRepositoryTest{

    // ...

    @Test
    void softDelete(){
        bookRepository.findAll().forEach(System.out::println);
        System.out.println(bookRepository.findById(3L));

        bookRepository.findByCategoryIsNull().forEach(System.out::println);

        bookRepository.findAllByDeletedFalse().forEach(System.out::println);
        bookRepository.findByCategoryIsNullAndDeletedFalse().forEach(System.out::println);
    }
}
```

<br/>



그런데 항상 deleted = false 를 항상 모든 쿼리에 지정해서 사용하기에는 불편함이 따릅니다. 이런 경우에는 `@Where` 를 사용하면 조금 더 유지보수가 쉬운 코드를 작성할 수 있습니다.<br/>

```java
@Entity
@Where(clause = "deleted = false") // 추가해준 부분 
public class Book extends BaseEntity{
    // ...
    
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @ToString.Exclude
    private Publisher publisher;

    // deleted 필드를 추가해줬다. 참고로 테이블에 `deleted` 컬럼 역시 추가해줘야 한다. 
    private boolean deleted;
}
```

이렇게 한 이후에 repository 테스트를 진행하면 book 엔티티 조회시에는 항상 deleted = false 인 데이터들만 select 됩니다.<br/>



## 요약

cascade 는 편리해보이지만 남용하게 될 경우 어려운 문제들을 겪게될 수 있습니다. cascade로 인해 발생할 수 있는 문제들은 트러블 슈팅 관련 챕터에서 정리 예정입니다.<br/>

<br/>

