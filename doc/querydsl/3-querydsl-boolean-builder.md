# BooleanBuilder - 동적 쿼리

- BooleanExpression 을 where 절에 모두 전달해주어서 where 절에서 파악하도록 하는 방식
- BooleanBuilder 를 이용해서 동적쿼리를 만들어내는 방식

where 조건절을 동적으로 구성하는  동적쿼리를 만드는  두 가지 방법이 있습니다. 이 중 BooleanBuilder 를 사용시에는 NPE(Null Pointer Exception)을 조심해야 하기에 이 부분에 중점을 둔 null safe builder 역시 문서에 정리해두었고, null 값에 대해 비어있는 BooleanBuilder 객체를 생성하는 부분역시 주의해서 확인해주시기 바랍니다.  

간단한 코드의 경우 BooleanExpression 을 where 절에 모두 전달해서 where 절 조건문을 작성하는 방식이 좋지만 표현식을 재사용해야 할 경우에는 BooleanBuilder 를 사용하는 것을 추천합니다.<br/>

<br/>



## 참고자료

- [querydsl 동적쿼리 where문에 여러 메서드 사용 null처리 어떤식으로 하는게 좋을까요?](https://www.inflearn.com/questions/821819/querydsl-%EB%8F%99%EC%A0%81%EC%BF%BC%EB%A6%AC-where%EB%AC%B8%EC%97%90-%EC%97%AC%EB%9F%AC-%EB%A9%94%EC%84%9C%EB%93%9C-%EC%82%AC%EC%9A%A9-null%EC%B2%98%EB%A6%AC-%EC%96%B4%EB%96%A4%EC%8B%9D%EC%9C%BC%EB%A1%9C-%ED%95%98%EB%8A%94%EA%B2%8C-%EC%A2%8B%EC%9D%84%EA%B9%8C%EC%9A%94)
- supplier 를 이용한 nullSafeBuilder 예제
  - [Where 다중 파라미터 사용, Null 처리관련 질문입니다.](https://www.inflearn.com/questions/735908/where-다중-파라미터-사용-null-처리관련-질문입니다)
  - [강사님 where 다중 파라미터를 이용한 동적 쿼리 사용에 대한 질문입니다.](https://www.inflearn.com/questions/94056/강사님-where-다중-파라미터를-이용한-동적-쿼리-사용에-대한-질문입니다)
- [Where 다중 파라미터 사용, Null 처리관련 질문입니다.](https://www.inflearn.com/questions/735908/where-%EB%8B%A4%EC%A4%91-%ED%8C%8C%EB%9D%BC%EB%AF%B8%ED%84%B0-%EC%82%AC%EC%9A%A9-null-%EC%B2%98%EB%A6%AC%EA%B4%80%EB%A0%A8-%EC%A7%88%EB%AC%B8%EC%9E%85%EB%8B%88%EB%8B%A4)



<br/>



## BooleanBuilder

일반적으로 BooleanBuilder, where(BooleanExpression...) 을 이용하면 동적쿼리를 만들 수 있습니다.

- where(BooleanExpression...) 의 Boolean Expression 가변인자 활용
- BooleanBuilder

<br/>



### where(BooleanExpression...) 의 Boolean Expression 가변인자 활용

BooleanExpression 을 여러개를 where 절에 가변인자로 전달해주는 방식으로도 조합이 가능합니다.

```java
private List<Member> searchMember2(String usernameCond, Integer ageCond) {
    return queryFactory
            .selectFrom(member)
            .where(usernameEq(usernameCond), ageEq(ageCond))//메서드를 만들어서 한번에 처리도 가능, 조립가능!!
            .fetch();
}
private BooleanExpression usernameEq(String usernameCond) {
    return usernameCond != null ? member.username.eq(usernameCond) : null;
}
private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond) : null;
}
```

<br/>



### BooleanBuilder - null 인 조건은 제외하고 검색조건식 조합

기본적으로는 null 인 파라미터에 대해서 이렇게 조합가능합니다.

```java
private List<Member> searchMember1(String usernameCond, Integer ageCond) {
    BooleanBuilder builder = new BooleanBuilder();
    if (usernameCond != null) {
        builder.and(member.username.eq(usernameCond));
    }
    if (ageCond != null) {
        builder.and(member.age.eq(ageCond));
    }
    
    return queryFactory
        .selectFrom(member)
        .where(builder)
        .fetch();
}
```

<br/>



### BooleanBuilder - 비어있는 BooleanBuilder 객체 활용

여기서 더 발전시켜서 BooleanBuilder 에 null 을 전달해주어 비어있는 BooleanBuilder 를 생성하고, 이미 존재하는 조건식을 BooleanBuilder를 전달해주는 등의 방식으로도 동적쿼리를 생성이 가능합니다.

```java
private List<Member> searchMember3(String usernameCond, Integer ageCond) {
    return query.selectFrom(member)
            .where(allEq(usernameCond, ageCond))
            .fetch();
}

private Predicate usernameEq(String usernameCond) {
    return usernameCond != null ? member.username.eq(usernameCond) : null;
}

private Predicate ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond) : null;
}

private BooleanBuilder allEq(String usernameCond, Integer ageCond) {
    BooleanBuilder builder = new BooleanBuilder();
    return builder.and(usernameEq(usernameCond)).and(ageEq(ageCond));
}
```

<br/>



- new BooleanBuilder(Expression) : 특정 조건식에 대한 빌더를 구성합니다.
- BooleanBuilder(null) : 생성자에 null 을 줄 경우에는 비어있는 조건식이 되는데, null 을 인자로 넘겨줄 경우 NPE가 발생할 수 있다는 단점이 있습니다.
- BooleanBuilder() : 생성자에 아무 값도 주지 않으면 비어있는 조건식이 됩니다.



<br/>



### null-safe builder

참고

- [Where 다중 파라미터 사용, Null 처리관련 질문입니다.](https://www.inflearn.com/questions/735908/where-다중-파라미터-사용-null-처리관련-질문입니다)
- [강사님 where 다중 파라미터를 이용한 동적 쿼리 사용에 대한 질문입니다.](https://www.inflearn.com/questions/94056/강사님-where-다중-파라미터를-이용한-동적-쿼리-사용에-대한-질문입니다)

<br/>

BooleanBuilder 내에 null 이 전달되었을 때 의도치 않은 동작이 발생할 수 있습니다. 여기에 대한 해결책으로 비어있는 BooleanBuilder() 를 생성하는 null safe builder 를 생성해주면 좋습니다.<br/>



nvlBuilder

```java
private BooleanBuilder nvlBuilder(Supplier<BooleanExpression> supplier){
    try{
        return new BooleanBuilder(supplier.get());
    }
    catch(Exception e){
        return new BooleanBuilder();
    }
}
```

<br/>





### 문자열 데이터 BooleanBuilder 처리 

- 문자열이 비어있거나 null 일 경우에는 new BooleanBuilder()를 통해 비어있는 BooleanBuilder 를 리턴

```java
private BooleanBuilder productNmEq(String productNm){
    if(!StringUtils.hasText(productNm))
        return new BooleanBuilder();
    else
        return nvlBuilder(() -> randomComponentProduct.productNm.eq(productNm));
}
```

<br/>



### 리스트 데이터 BooleanBuilder 처리 

```java
private BooleanBuilder sectionIn(List<SectionType> sections){
    if(sections == null || sections.isEmpty())
        return new BooleanBuilder();
    else
        return nvlBuilder(() -> randomComponentProduct.section.in(sections));
}
```

<br/>



### Enum 데이터 BooleanBuilder 처리

Enum 은 Converter 를 미리 만들어둔 상태로 아래의 조건식을 사용하였습니다.

```java
private BooleanBuilder viewYnEq(ViewYn viewYn){
    if(viewYn == null)
        return new BooleanBuilder();
    else
        return nvlBuilder(()-> randomComponentProduct.viewYn.eq(viewYn));
}
```

<br/>



### 전체 조건식 조합

위에서 살펴본 문자열,리스트,Enum 을 처리하는 식들을 BooleanBuilder 를 사용세 조합하는 식은 아래와 같습니다.

```java
private BooleanBuilder searchFilter(SomethingCommercialEventForm searchForm){
    return productNmEq(searchForm.getProductNm())
            .and(productCdEq(searchForm.getProductCd()))
            .and(sectionIn(searchForm.getSections()))
            .and(viewYnEq(searchForm.getViewYn()));
}
```



쿼리에서는 아래와 같이 사용하게 됩니다.

```java
public List<CommercialEvent> productList(SomethingCommercialEventForm searchForm) {
    return queryFactory.select(CommercialEvent)
            .from(CommercialEvent)
            .where(searchFilter(searchForm))
            .fetch();
}
```

<br/>

