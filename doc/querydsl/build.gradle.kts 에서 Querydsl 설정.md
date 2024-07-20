# build.gradle.kts 에서 Querydsl 설정

## 참고자료

- [Gradle 7 Kotlin DSL (build.gradle.kts) Querydsl 5 설정](https://www.inflearn.com/chats/829853/gradle-7-kotlindsl-build-gradle-kts-querydsl-5-%EC%84%A4%EC%A0%95)

<br/>



## build.gradle.kts

`import com.ewerk.gradle.plugins.tasks.QuerydslCompile` 을 꼭 빠뜨리지 않아야 합니다.

```kotlin
import com.ewerk.gradle.plugins.tasks.QuerydslCompile

plugins {
	java
	id("org.springframework.boot") version "3.3.1"
	id("io.spring.dependency-management") version "1.1.5"
	id("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
}

group = "io.summary.jpa"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

val querydslVersion = "5.0.0:jakarta"

repositories {
	mavenCentral()
}

dependencies {
	// ...

	implementation ("com.querydsl:querydsl-jpa:${querydslVersion}")
	annotationProcessor("com.querydsl:querydsl-apt:${querydslVersion}")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    
    // ...
}

tasks.withType<Test> {
	useJUnitPlatform()
}

//querydsl
// val querydslDir = "src/main/generated"
val querydslDir = "$buildDir/generated/querydsl"

querydsl {
	jpa = true
	querydslSourcesDir = querydslDir
}
sourceSets.getByName("main") {
	java.srcDir(querydslDir)
}
configurations {
	named("querydsl") {
		extendsFrom(configurations.compileClasspath.get())
	}
}
tasks.withType<QuerydslCompile> {
	options.annotationProcessorPath = configurations.querydsl.get()
}
```

<br/>



## build + QClass 생성

QClass 환경을 초기화 합니다.

- querydsl → cleanQuerydslSourcesDir
- querydsl → initQuerydslSourcesDir

<br/>



QClass 를 생성합니다.

- other → compileQuerydsl















