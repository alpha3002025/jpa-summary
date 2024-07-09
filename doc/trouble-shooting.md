트러블 슈팅


### `Access to DialectResolutionInfo cannot be null when 'hibernate.dialect' not set ...` 

JPA 는 디버깅시에 에러 로그가 엉뚱한 로그를 출력하는 경우가 많습니다. 이 부분을 주의해야 합니다.<br/>

`'hibernate.dialect' not set ...` 이라는 로그는 dialect 를 선언하지 않아서 생기는 에러같지만, dialect 문제는 아닙니다.
Database 가 구동되어 있지 않아서 접속이 안될때 생기는 에러 로그입니다.<br/>

해결방법
- mysql 을 구동시키거나, mysql 커넥션 url 이 정상적으로 지정되어 있는지 확인합니다.


### `failed to lazily initialize a collection of role: {패키지명}.{엔티티}.{연관관계 필드}, could not initialize proxy - no Session...`

`could not initialize proxy` 와 같은 문구는 lazy loading 이 제대로 적용되지 않아서 생기는 에러입니다. 이 오류 메시지 역시 실제 JPA 코딩을 하다보면 굉장히 자주 접하는 메시지입니다.<br/>
이 경우 해결책은 아래의 두가지 이며, 이 에러에 대해서는 이 글의 후반부에서 정리하고 있다.<br/>

- 테스트 코드 실행시 발생한 에러라면 @Transactional 을 테스트 메서드에 붙인다.
- 또는 연관관계(OneToOne, OneToMany, ManyToMany, ManyToOne)로 선언된 필드 들에 대해 @ToString.Exclude 를 지정해준다. (ToString 대상에서 제외시켜준다.)

<br/>

hibernate 에서 말하는 Session 개념은 JPA 기준으로 Transaction 의 범위와 거의 유사한 범위로 취급된다. Session 이 없다는 의미는 'Transaction 이 존재하지 않는다.' 와 거의 유사하게 취급된다.<br/>
Session 은 Transaction 과 조금은 범위가 다르긴 합니다. (Session 은 네트워크, Transaction 은 데이터 취급 단위를 의미)<br/>
<br/>



