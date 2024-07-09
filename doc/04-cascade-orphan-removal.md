# 영속성 전이 (Cascade), 고아 제거 (orphan removal) 속성

Cascade 의 영어 단어 의미는 "작은 폭포"입니다. 영속성에 있어서 Cascade 의 의미는 다음과 같습니다.<br/>
- "폭포가 위에서 물을 받으면 아래로 흘려보내듯 영속성도 하나의 객체에서 다른 객체로 영속성을 흘려보내준다는 의미"

<br/>

OneToOne, OneToMany, ManyToMany, ManyToOne 등으로 정의된 객체에서 모두 cascade 를 사용가능합니다.<br/>

연관관계의 다른 객체들의 저장/삭제/수정을 직접 Repository 의 메서드를 직접 호출해서 저장하는 것은 다소 관계형 데이터베이스 같은 느낌이 있습니다.<br/>

Cascade 의 속성을 잘 활용하면 엔티티의 변경이 발생시 연관관계에 해당하는 엔티티에게도 전이를 일으켜서 저장/삭제/수정 구문을 하드코딩하지 않고도 엔티티 매니저가 저장/삭제/수정을 수행하도록  할 수 있습니다.<br/>



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
- repository.save() 를 Book 엔티티 save() 직전에 호출하도록 정의해줍니다.
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



## Cascade.PERSIST, Cascade.REMOVE

실무에서는 직접 Repository 에 접근해서 save(), remove() 하기보다는 Cascade.PERSIST, Cascade.REMOVE 를 이용하는 경우가 많습니다.<br/>

참고로 cascade 는 아래와 같이 `default = {}` 로 선언되어 있기 때문에 연관관계 필드에 cascade 속성을 지정하지 않으면 영속성전이가 일어나지 않습니다.

```java
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface OneToMany {
    // ...

    /** (Optional) ... */
    CascadeType[] cascade() default {}; // cascade 

    // ...
}
```

<br/>



## 고아 제거 속성 (orphanRemoval)

퇴근 후 정리 예정







