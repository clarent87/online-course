# 1부. JUnit 5

## JUnit 5 소개

- 2017년 10월에 공개됨
- spring boot 2.2이상의 default 는 JUnit 5
- 자바8 이상을 필요로함
- JUnit 5의 모듈
  - Jupiter: TestEngine API 구현체로 JUnit 5를 제공.
  - > 주로 이것을 본다고 함. 본강좌에서

## JUnit 5 시작하기

- 프로젝트를 생성했는데, 강좌와는 다르게 spring 2.7.0으로 일단 만듬. 강좌는 2.2.1버전
- spring 이니셜라이저가 만들어 주는 기본 메인 함수 모두 삭제함. src/test 모두..

- junit 5 부터는 class, method 모두 public 안붙여도됨
- spring project의 의존성을 보면
  - spring-boot-starter-test 밑에 jupiter가 들어온것을 알수 있음
  - > 강사 화면에서는 junit 4 TestEngine API 구현체인 vintange는 exclude한거 볼수 있음. 어짜피 필요없어서 인듯

- tip
  - spring-boot-maven-plugin 빨간줄 나오면 `<version>${parent.version}</version>` 붙이면됨

    ```xml
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${parent.version}</version>
                </plugin>
            </plugins>
        </build>

    ```

- 기본 애노테이션
  - @Test
  - @BeforeAll / @AfterAll
    - 모든 test 호출전 한번만 호출됨. 반드시 static method여야 함
    - return type 이있으면 안되고 prviate도 안됨
    - 그냥 `static void` 로 작성하면됨
    - > junit 4에서는 @BeforClass/@AfterCalss
  - @BeforeEach / @AfterEach
    - 각각의 test가 호출되는 이전 이후에 한번씩 호출됨
    - > junit 4에서는 @Befor/@After
  - @Disabled
    - > junit 4에서는 @Ignored
  
- junit4로 작성된 test를 전부 junit5로 바꾸고 싶다?
  - 그러지 않아도됨
  - junit4 코드도 junit5기반으로 충분히 실행가능. -> vintage 모듈 때문인듯

## JUnit 5 테스트 이름 표시하기

- junit 문서에는 아래 내용외에도 다양한 테스트 이름 표기 방법이 있다고함.

- junit5의 기본 테스트 이름 표기 전략
  - method 이름을 표기해준다.
  - > 그 test 실행하면 좌측 아래 나오는 이름 말함.
  - 보통 test case에는 CamalCase보다는 snake_case로 이름을 작성함 👍
    - > 근데 `@DisplayName` 이 있으니 junit5에서는 이제 위처럼 할 필요는 없을듯.

- 유용한 annotation
  - `@DisplayNameGeneration`
    - Method와 Class 레퍼런스를 사용해서 테스트 이름을 표기하는 방법 설정.
    - 기본 구현체로 ReplaceUnderscores 제공
    - > type(class) 또는 method에 붙일수 있음

  ```java
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class StudyTest {
        @Test
        void create_new_study() { // 이름이 create new study 로 나옴. 즉 언더스코어를 공백으로 치환해줌
            Study study = new Study();
            assertNotNull(study);
            System.out.println("create");
        }
    }

  ```

  - `@DisplayName`
    - 어떤 테스트인지 테스트 이름을 보다 쉽게 표현할 수 있는 방법을 제공하는 애노테이션.
    - @DisplayNameGeneration 보다 우선 순위가 높다.
    - > 이게 더 나은 방법
    - > 김영한님 강좌에서 나온 방법 👍👍
    - > 강좌에서도 이거를 권장.

## JUnit 5 Assertion

- assertEqulas(expected, actual)
- assertNotNull(actual)
- assertTrue(boolean)
- assertAll(executables...)
  - 모든 확인 구문 확인
- assertThrows(expectedType, executable)
  - expectedType 은 예외.class type을 말함.
  - executable는 functional interface임.
  - return값으로 exception받아낼수 있음. 이걸로 에러 message 확인도 가능
- assertTimeout(duration, executable)
  - 특정 시간 안에 실행이 완료되는지 확인
  - > 단점. executable의 로직들이 끝나야 test가끝남.
  - > 즉 duration안에 executable 실행이 끝나지 않았다면 바로 중지하는게 좋은데..
  - > 이 기능은 assertTimeoutPreemptively가 가짐

- assertTimeoutPreemptively 사용시 주의점
  - executable이 독립된 thread에서 동작. 즉 만약 executable이 threadlocal을 쓰는경우 동작이 예상과는 다를수 있다고함
  - > spring transactional의 기본 전략이 threadlocal을 이용하는 거라고함 -> spring transactional이 제대로 동작안함
  - > 이 내용... 어딘가에서 본거 같은데... study쪽에 아마 있었을거 같음.
  
마지막 매개변수로 Supplier<String> 타입의 인스턴스를 람다 형태로 제공할 수 있다.  
복잡한 메시지 생성해야 하는 경우 사용하면 실패한 경우에만 해당 메시지를 만들게 할 수 있다.  
  
AssertJ, Hemcrest, Truth 등의 라이브러리를 사용할 수도 있다.  

```java
    // 예시. assertAll 특징 보면 좋음
    void create_new_study() {
        Study study = new Study();
        assertNotNull(study);
        // 기대하는 값을 먼저 적어야한다.
        assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값은 DRAFT여야 한다. ");

        // 위와 같은 경우는 assertNotNull 이 실패하면 assertEquals가 수행되지 않음.
        // 근데 assertAll은 모든것을 수행해줌
        assertAll(
                ()->assertNotNull(study),
                ()-> assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값은 DRAFT여야 한다. ")
        );
    }

```

## JUnit 5 조건에 따라 테스트 실행하기

특정한 조건을 만족하는 경우에 테스트를 실행하는 방법.
이를테면, 특정한 java 버전이나, 혹은 특정한 환경변수가 있을때만 test를 실행해야할때  
> 이건 쓸만 하겠는데?

- jupiter의 Assumptions에서 제공하는 api를 아래 처럼 사용

```java
    @Test
    @DisplayName("스터디 만들기")
    void create_new_study_again() {

        // 이게 만족해야, 그다음 assert들이 실행됨
        // 즉 만족을 안하면, 다음 코드들은 실행이 안되는 형태 인듯
        assumeTrue("LOCAL".equals(System.getenv("TEST_ENV"))); // intellij에서는 환경변수를 intellij실행시만 받아옴.
                                                                // 즉 os의 환경변수 수정시, intellij를 껏다 키지 않으면
                                                                // 반영이 안됨.
        assertTrue(true);
        
        System.out.println("create1");
    }

```

- org.junit.jupiter.api.Assumptions.*
  - assumeTrue(조건)
  - assumingThat(조건, 테스트)

조건 처리를 위 처럼 코드로 해도 되고, 아니면 아래 어노테이션을 Test method에 붙여서 처리해도 됨.

- @Enabled___와 @Disabled___
  - > 아래 문자가 위 어노테이션 빈칸에 들어갈 문자들
  - OnOS
  - OnJre
  - IfSystemProperty
  - IfEnvironmentVariable
  - If

## JUnit 5 태깅과 필터링 👍

테스트 그룹을 만들고 원하는 테스트 그룹만 테스트를 실행할 수 있는 기능.
> 이거 spring category.. 그거랑 같은건가?
  
- 예시
  - unit test, integration test 그룹을 만들고 local에서는 unit test만 실행
  - 느린 test, 빠른 test를 tag로 그룹화하고 느린 test는 ci환경에서만 진행

- intellij 세팅
  - 기본적으로 intellij에서는 모든 test를 실행해주니까, tag로 필터릴 해서 돌릴려면  
  - 세팅을 바꿔 줘야함 -> 문서 참조

- 메이븐에서 test 필터링 하려면? 아래 dependency 이용

  ```xml
    <plugin>
        <artifactId>maven-surefire-plugin</artifactId>
        <configuration>
          <!-- fast, slow 하나만 선택 하던가 두개다 선택 -->
            <groups>fast | slow</groups> 
        </configuration>
    </plugin>

  ```

  - 이 세팅은 build tool 즉 mvn으로 build 할때 먹히는거..
  - intellj에서 test 실행은 intellij 세팅이 먹힘
  - 실제 사용시에는 아래와 같이 maven 세팅을 해서 사용 가능

  ```xml
      <profiles>
          <profile>
              <id>default</id>
              <activation>
                  <activeByDefault>true</activeByDefault>
              </activation>
              <build>
                  <plugins>
                      <plugin>
                          <artifactId>maven-surefire-plugin</artifactId>
                          <configuration>
                              <groups>slow</groups>
                          </configuration>
                      </plugin>
                  </plugins>
              </build>
          </profile>
      </profiles>

  ```

  - 즉 profile을 통해서 local에서는 fast를 돌리고, 다른 profile에서는 slow를 돌리는 형태로.. 작업 가능.
    - > 그럼 regression profile을 만들어서 slow를 돌리는 형태.. 고려해야겠네.
    - > spring jar 실행에서는 pom의 profile이 영향을 주지는 않았음. -> java -jar 실행에서는 maven의 옵션을 못줘서 그런듯
    - `./mvnw -P test`
      - 여기서 -P가 프로파일을 선택하는 옵션임.  

- @Tag
  - 테스트 메소드에 태그를 추가할 수 있다.
  - 하나의 테스트 메소드에 여러 태그를 사용할 수 있다.
  - tag 관련 expression은 아래 링크 참조
    - https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions

## JUnit 5 커스텀 태그

JUnit 5 애노테이션을 조합하여 커스텀 태그를 만들 수 있다  
  
위에서 봣던 annotation들은 meta annotation으로 사용가능
즉, annotation만들때 사용할수 있다는 것
  
아래처럼 커스텀 어노테이션 만들수 있음

```java
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  @Tag("fast")
  @Test
  public @interface FastTest {
  }
```

```java
    @FastTest
    @DisplayName("스터디 만들기 fast")
    void create_new_study() {}

    @SlowTest
    @DisplayName("스터디 만들기 slow")
    void create_new_study_again() {}


```
  
이렇게 어노테이션으로 만들어 두면, @Tag("오타") 에 문자열 넣을때 오타로 인한 실수를 줄일수 있음.  
> 당연하겠지만, 커스텀 어노테이션 썻을때에도, intellj 또는 maven pom의 tag/group 세팅 해줘야함. 그래야 원하는
> Tag의 test를 실행 시킬수 잇음

## JUnit 5 테스트 반복하기 1부

> 잘 쓸거 같지는 않음

테스트를 반복적으로 실행하는 방법
  
- 이를테면 매번 test를 실행할때마다 random값을 쓴다던가, test가 실행되는 타이밍에 따라 달라질수 있는 조건.. 등이 있다면?
  - 테스트를 여러번 돌려보는것이 좋다
  - 이때 쓰는 기능

- @RepeatedTest
  - 반복 횟수와 반복 테스트 이름을 설정할 수 있다.
    - {displayName}
    - {currentRepetition}
    - {totalRepetitions}
  - RepetitionInfo 타입의 인자를 받을 수 있다.

- @ParameterizedTest
  - 테스트에 여러 다른 매개변수를 대입해가며 반복 실행한다.
    - {displayName} // 이거 annotation attribute 값에 씀. 예시 보면 안다.
    - {index}
    - {arguments}
    - {0}, {1}, ...

- 예시

```java

    @DisplayName("스터디 만들기")
    @RepeatedTest(value = 10, name = "{displayName},{currentRepetition}/{totalRepetitions}")
    void repeatTest( RepetitionInfo repetitionInfo) { // 인자로 RepetitionInfo를 받을수 있음. 이걸로 현재 몇번째 반복인지 확인 가능
        System.out.println("test" + repetitionInfo.getCurrentRepetition());
    }

    // junit 5는 기본으로 제공해주는 어노테이션
    // junit 4는 서드 파티 라이브러리를 사용해야함
    // 테스트에 여러 다른 매개변수를 대입해가며 반복 실행할때 씀
    // 사용할 파라메터들을 정의하는 방법은 다양함. 여기서는 ValueSource 이용
    @DisplayName("스터디 만들기")
    @ParameterizedTest( name = "{index} {displayName} message = {0}") // test method의 param을 {0}으로 받음.
    @ValueSource(strings = {"날씨가", "많이", "추워지고", "있습니다."})
    void parameterizedTest(String message) {
        System.out.println("message = " + message);
    }


```

## JUnit 5 테스트 반복하기 2부

## JUnit 5 테스트 인스턴스

## JUnit 5 테스트 순서

## JUnit 5 junit-platform.properties

## JUnit 5 확장 모델

## JUnit 5 마이그레이션

## JUnit 5 연습 문제
