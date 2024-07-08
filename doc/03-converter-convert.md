## Converter, convert

JPA 는 ORM 기술을 인터페이스로 추상화한 기술입니다. JPA를 구현한 구현체는 여러가지가 있지만 이중 가장 보편적인 구현체는 hibernate 입니다.<br/>

데이터베이스의 레코드를 자바의 객체로 변환시켜주는 작업이 필요한데 이 작업을 할 때 DB의 데이터와 다를 경우 어떤 방식으로 매핑이 이뤄지는지 살펴봐야 합니다.<br/>

이번 문서에서는 @Converter 어노테이션을 통해서 별도의 데이터 변환 로직 없이도 데이터베이스의 레코드가 엔티티에 매핑되게끔 하는 것을 살펴보는 예제들을 살펴봅니다.<br/>

<br/>

**e.g. @Enumerated(value = EnumType.STRING)**<br/>

우리가 자주 사용하는 @Enumerated(value = EnumType.STRING) 역시 내부적으로는 @Converter 를 사용합니다. 내부적인 구현을 찾아보다 보면 OrdinalEnumConverter,  NamedEnumValueConverter 가 있습니다. 

- OrdinalEnumConverter 는 Ordinal 값을 기반으로 Enum 으로 변환해주는 역할을 합니다.

- NamedEnumValueConverter 는 enum 에 대한 name 을 기반으로 Enum 으로 변환해주는 역할을 합니다.

<br/>



## e.g. 레거시 코드 

book 이라는 엔티티에 대한 레거시 코드가 있다고 해보겠습니다. 이 엔티티는 책의 상태값을 저장하는 필드 `status` 가 int 타입으로 정의되어 있습니다. 처음부터 enum 타입, embedded 타입의 클래스로 엔티티를 정의했다면 이렇게 raw 레벨의 int 타입 코드를 하드코딩해서 사용하지 않았을 것입니다. <br/>

하지만, 현실세계의 많은 레거시 코드들이 이렇게 하드코딩 기반의 int 타입 코드를 사용하는 곳이 많기도 하고 다른 시스템에서 int 값 기반의 상태코드를 사용하는 경우에도 하드코딩 기반의 int 타입 코드를 사용하기도 합니다.

```java
@Entity
public class Book {
    // ...
    
    // 판매상태
    private int status;

    public boolean isDisplayed(){
        return status == 200;
    }

}
```

<br/>



data.sql

```sql
insert into book(`id`, `name`, `publisher_id`, `deleted`, `status`) values (1, '논어', 1, false, 100);

insert into book(`id`, `name`, `publisher_id`, `deleted`, `status`) values (2, '인슐린 저항성에 대해', 1, false, 200);

insert into book(`id`, `name`, `publisher_id`, `deleted`, `status`) values (3, '혈당 스파이크를 잡아라', 1, true, 100);
```

<br/>



테스트코드

- 테스트 코드에서는 단순하게 select 해보고, 값을 프린트해봅니다.

```java
@SpringBootTest
public class BookRepositoryTest{
    @Autowired
    private BookRepository bookRepository;

    // ...

    @Test
    public void test_converter(){
        bookRepository.findAll().forEach(System.out::println);
    }
    
    // ...

}
```

<br/>

이렇게하면 출력결과가 아래와 같이 나타납니다.

```
Book(id=1, ... , status=100)
Book(id=2, ... , status=200)
```

<br/>



## 레거시 코드를 ORM 스타일로 전환

상태코드를 int 로 저장하는 것은 ORM 을 사용할 때 의미가 없습니다. int 를 사용하는 대신 객체로 변환되도록 레거시 코드를 전환하는 예제를 살펴보겠습니다.<br/>

**BookStatus.java**

```java
package com....작업패키지...repository.dto;

public class BookStatus{
    private int code;
    private String description;

    public BookStatus(int code){
        this.code = code;
        this.description = parseDescription(code);
    }

    public boolean isDisplayed(){
        return code == 200;
    }

    private String parseDescription(int code){
        switch(code){
            case 100:
                return "판매종료";
            case 200:
                return "판매중";
            case 300:
                return "판매보류";
            default:
                return "미지원";  
        }
    }
}
```

책의 상태를 표현는 역할의 Status 를 정의합니다.<br/>

<br/>



**Book.java**

```java
@Entity
public class Book{
    // ...

    // 판매상태
    // private int status; // 주석처리

    // 이 때 인텔리제이에서도 'Basic' attribute type shoulde not be 'BookStatus' 라는 경고문구가 나타난다.
    // 인텔리제이의 JPA 관련된 설정에 Database가 연결되어 있을 때, 데이터베이스의 타입과 JPA 의 타입이 안맞을 경우 나는 경고문구다.
    private BookStatus status;

    // 이전의 isDisplayed() 는 주석처리한다.
    // public boolean isDisplayed(){
    //     return code == 200;
    // }

    // ... 
}
```

`private BookStatus status;`  를 추가하면서 인텔리제이에서도 경고문구를 통해 데이터베이스 컬럼과 Java 타입이 안맞는다는 경고를 합니다. <br/>

이 경고는 Converter 를 작성해서 매핑해주면 해결됩니다.<br/>



**BookStatusConverter.java**

Converter 를 작성합니다. 적당한 패키지 내에 converter 패키지를 생성하고 이 converter 패키지 아래에 BookStatusConverter 라는 클래스를 생성해주고 아래와 같이 정의해줍니다.<br/>

```java
@Converter
public class BookStatusConverter implements AttributeConverter<BookStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BookStatus attribute){
        return attribute.getCode();
    }

    @Override
    public BookStatus convertToEntityAttribute(Integer dbData){ // (1)
        return dbData != null ? new BookStatus(dbData) : null; // db 의 데이터가 null 일 경우에 대한 null 처리
    }
}
```



(1) 

- Converter 는 Database 와 밀접하게 관련되어 있기 때문에 Converter 내에서 null, not null 관련 NPE가 최대한 발생하지 않도록 미연에 방지하는 것이 중요합니다. 따라서 null 처리를 확실하게 해줘야 합니다.

- 3항 연산자의 경우 클린코드 등에서 지양하도록 권고하고 있기는 하지만, 간단한 식이기에 직관적으로 알아볼 수 있기에 3항연산자를 사용했다.



**Book.java**

다 되었다면 다시 Book 엔티티의 코드로 돌아와서 아래와 같이 작성해줍니다.

```java
@Entity
public class Book{
    // ...

    // 판매상태
    @Convert(converter = BookStatusConverter.class) // (1)
    private BookStatus status;

    // 이전의 isDisplayed() 는 주석처리한다.
    // public boolean isDisplayed(){
    //     return code == 200;
    // }

    // ... 
}
```

(1)

- @Converter 가 아니라 @Convert 로 작성하셔야 합니다. 혼동하는 사람들이 많은 편입니다.

- @Convert 내의 converter 속성에 BookStatusConverter.class 를 추가해줬습니다.



테스트 코드를 실행해봅니다.

```java
@SpringBootTest
public class BookRepositoryTest{
    @Autowired
    private BookRepository bookRepository;

    // ...

    @Test
    public void test_converter(){
        bookRepository.findAll().forEach(System.out::println);
    }
    
    // ...

}
```

출력결과는 아래와 같이 나타납니다.

```
Book(id=1, ... , status=BookStatus(code=100, dsecription=판매종료))
Book(id=2, ... , status=BookStatus(code=200, description=판매중))
```

<br/>



## 참고\) AttributeConverter

AttributeConverter 는 아래와 같이 정의되어 있습니다.

```java
package javax.persistence;

/**
 * A class that implements this interface can be used to convert 
 * entity attribute state into database column representation 
 * and back again.
 * Note that the X and Y types may be the same Java type.
 *
 * @param <X>  the type of the entity attribute
 * @param <Y>  the type of the database column
 */
public interface AttributeConverter<X,Y> {

    /**
     * Converts the value stored in the entity attribute into the 
     * data representation to be stored in the database.
     *
     * @param attribute  the entity attribute value to be converted
     * @return  the converted data to be stored in the database 
     *          column
     */
    public Y convertToDatabaseColumn (X attribute);

    /**
     * Converts the data stored in the database column into the 
     * value to be stored in the entity attribute.
     * Note that it is the responsibility of the converter writer to
     * specify the correct <code>dbData</code> type for the corresponding 
     * column for use by the JDBC driver: i.e., persistence providers are 
     * not expected to do such type conversion.
     *
     * @param dbData  the data from the database column to be 
     *                converted
     * @return  the converted value to be stored in the entity 
     *          attribute
     */
    public X convertToEntityAttribute (Y dbData);
}
```

<br/>



## 실제 DB 값도 확인해보기

DB에서 직접확인할수도 있겠지만, 실제 DB 값을 NativeQuery 를 통해서 확인해봅니다.<br/>

BookRepository 에 아래의 코드를 추가해줍니다.<br/>

**BookRepository.java**<br/>

```java
public interface BookRepository extends JpaRepository<Book, Long> {

    // ...

    // (1)
    @Query(value = "select * from book order by id desc limit 1", nativeQuery = true)
    // (2)
    Map<String, Object> findRawRecord();

}
```

(1)

- nativeQuery = true 를 주었고, 쿼리 역시 ORM 기반의 JPQL이 아닌 native Query 를 입력했습니다.

(2)

- `Map<String, Object>` 타입 반환을 하도록 해줬습니다. 이렇게 한 이유는 JPA 가 매핑해주는 값이 아닌 실제 값이 어떤지 확인을 위해서입니다.

<br/>



이번에는 실제 값 확인을 위해 위에서 작성한 네이티브 쿼리를 이용해 테스트 코드를 수정해봅니다.

BookRepositoryTest.java

```java
@SpringBootTest
public class BookRepositoryTest{
    @Autowired
    private BookRepository bookRepository;

    // ...

    @Test
    public void test_converter(){
        bookRepository.findAll().forEach(System.out::println);

        Book book = new Book();
        book.setName("A Book on C");
        book.setStatus(new BookStatus(200));

        bookRepository.save(book);

        System.out.println(bookRepository.findRawRecord().values());

        bookRepository.findAll().forEach(System.out::println);
    }
    
    // ...

}
```

<br/>

출력결과

```
[4, 2024-07-07 11:15:00.223, 2024-07-07 11:15:00.223, null, null, false, A Book on C, 200, null]
```

<br/>



## AttributeConverter 구현시 반드시 양측의 convert 함수를 작성하자

실무에서 어떤 레거시 시스템을 전환하면서 기존 DB를 읽어들일 것이라고만 생각해서 아래와 같이 entity → DB 컨버팅 함수를 정의하지 않으면 영속성컨텍스트에 의해 null 값이 저장될 수 있는 장애가 발생하게 됩니다. <br/>

```java
@Converter
public class BookStatusConverter implements AttributeConverter<BookStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BookStatus attribute){
        return null; // (1)
    }

    @Override
    public BookStatus convertToEntityAttribute(Integer dbData){ 
        return dbData != null ? new BookStatus(dbData) : null; 
    }
}
```

컨버터로 값을 읽어오는 것 까지는 정상적으로 불러오겠지만 이후에 영속성 컨텍스트는 값을 새로 불러온 값이 기존 값과 다르기에 엔티티 캐시를 새로 업데이트 해줍니다. 따라서 위의 코드는 status 가 정상적인 값이 null 로 저장되는 현상이 발생할 수 있습니다.



테스트코드

```java
@SpringBootTest
public class BookRepositoryTest{

    // ...

    @Test
    public void test_converter(){
        bookRepository.findAll().forEach(System.out::println);
        bookRepository.findAll().forEach(System.out::println);
    }

    // ...
}
```

출력결과는 아래와 같이 `BookStatus` 로 잘 매핑되어서 잘 읽어들였습니다.

```
Book(id=1, ... , status=BookStatus(code=100, ...))
Book(id=2, ... , status=BookStatus(code=200, ...))
```

<br/>



이번에는 @Transactional 이 붙어있는 로직을 아래와 같이 작성합니다.

```java
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    // ...

    @Transactional
    public List<Book> getAll() {
        List<Book> books = bookRepository.findAll();

        // 출력구문 (1) 
        books.forEach(System.out::println);

        return books;
    }
}
```

<br/>



테스트코드입니다.

```java
@SpringBootTest
class BookServiceTest{
    @Autowired
    private BookService bookService;

    @Test
    void converterErrorTest() {
        bookService.getAll();

        // 출력구문 (2)
        bookRepository.findAll().forEach(System.out::println);
    }
}
```



출력구문(1) 에 해당하는 로그에서는 당연하게도 아래와 같이 잘 읽어들입니다.<br/>

하지만 @Transactional 을 벗어나면서 update SQL 이 실행되고<br/>

다시 데이터베이스로부터 데이터를 읽어들이면 status = null 로 변경되어 있는 것을 확인 가능하다.<br/>

<br/>



```plain
Book(id=1, ... , status=BookStatus(code=100, ...))
Book(id=2, ... , status=BookStatus(code=200, ...))

-- (1)

update book
set updated_at=?, ...
where id = ?

update book
set updated_at=?, ...
where id = ?

-- (2)

Book(id=1, ... , status=null)
Book(id=2, ... , status=null)
```



(1)
- @Transactional 이 완료된 시점입니다.
- 이 시점에 영속성 컨텍스트는 해당 엔티티 값 중 변경된 내용이 있는지 없는지 체크를 수행합니다.
- 만약 변경된 내용이 있다면 그 데이터를 Database 에 영속화합니다.
- 그런데 converter 중 convertToDatabaseColum() 에서 return null 을 하고 있는 부분으로 인해 JPA 는 이 부분을 통해 엔티티가 이전 값과 다르다고 판정을 하게 됩니다. (내부로직으로는 Converter 를 통해 Database 값으로 변환해보는 동작을 수행하는 것으로 추측됩니다.)
- 요약해보면 Converter 가 덜 구현되어 있어서 JPA는 @Transactional 전/후의 값이 다르다고 판정 후 Update 쿼리를 수행하게 됩니다. 즉 변경감지가 일어나게 된다. 이때 converter 내의 convertToDatabaseColumn() 의 return null 을 하고 있기에 update 쿼리 시에 해당 컬럼을 null 로 처리하게 됩니다.
- 이런 이유로 update 구문이 발생하게 됩니다.



(2)

- 컨버터 작성시 convertToDatabaseColumn() 에서 return null 을 하고 있습니다.



## autoApply 를 사용해보자

autoApply 를 사용해서 좋은 경우도 있고 나쁜 경우도 있습니다. 개인적으로 autoApply 를 적용하는 것을 선호하는 것은 아니지만, 예제를 작성해보면 아래와 같습니다.<br/>

BookStatus 를 아래와 같이 지정했다고 해보겠습니다.

```java
@Converter(autoApply = true) // (1)
public class BookStatusConverter implements AttributeConverter<BookStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(BookStatus attribute){
        return attribute.getCode();
    }

    @Override
    public BookStatus convertToEntityAttribute(Integer dbData){ 
        return dbData != null ? new BookStatus(dbData) : null; // db 의 데이터가 null 일 경우에 대한 null 처리
    }
}
```

(1)

- `autoApply = true` 를 지정해주었습니다.



Book.java

다시 Book 엔티티의 코드로 돌아와서 아래와 같이 작성해줍니다.

```java
@Entity
public class Book{
    // ...

    // 판매상태
    // @Convert(converter = BookStatusConverter.class) // (1)
    private BookStatus status;

    // ...
}
```

<br/>



BookRepositoryTest.java

```java
@SpringBootTest
public class BookRepositoryTest{
    @Autowired
    private BookRepository bookRepository;

    // ...

    @Test
    public void test_converter(){
        bookRepository.findAll().forEach(System.out::println);

        Book book = new Book();
        book.setName("A Book on C");
        book.setStatus(new BookStatus(200));

        bookRepository.save(book);

        System.out.println(bookRepository.findRawRecord().values());

        bookRepository.findAll().forEach(System.out::println);
    }
    
    // ...

}
```



출력결과

```
[4, 2024-07-07 11:15:00.223, 2024-07-07 11:15:00.223, null, null, false, A Book on C, 200, null]
```



## autoApply 사용시 주의점

autoApply 는 편리한 면이 있지만 주의할 점이 있습니다.<br/>

BookStatusConverter 는 BookStatus 라는 특정 타입에 대한 Converter 이지만 만약 IntegerConverter, StringConverter 같은 것들을 정의했을때 이 것들을 autoApply 를 적용할 경우 의도하지 않은 다른 integer, varchar, number 타입의 테이블에도 데이터가 컨버팅되어 의도하지 않은 저장작업이 발생한다는 점입니다.<br/>

BookStatus 처럼 별개의 클래스를 생성한 경우, 특정 타입에 대한 용도가 분명하고 어디에만 적용된다는 점이 명확하기 때문에 autoApply 를 사용하는 것이 문제가 되지 않습니다.<br/>

하지만 일반적인 데이터 타입인 Integer, String 에 대해서 autoApply 를 사용하게 될 경우 아래와 같이 해주어야 합니다.<br/>

- 먼저 해당 @Converter 내에서는 autoApply 를 false 로 해두어 autoApply 를 끈다. 

- 그리고 각각의 컬럼에 @Convert(converter = Integer.class) 를 개별적으로 따로 지정해주어야 한다.