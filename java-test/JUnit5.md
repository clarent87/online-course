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
  - 인텔리제이 태그 세팅에서 tag에 expression가능 👍
    - 즉 !같은거 사용가능

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
    - <https://junit.org/junit5/docs/current/user-guide/#running-tests-tag-expressions>

- 추가
  - JUnit4의 @Categry가 JUnit5에서 @Tag로 변경됨
  - <https://www.baeldung.com/junit-5-migration>

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

`@ParameterizedTest` 를 좀더 자세히 살펴봄

- `@ValueSource(strings = {"날씨가", "많이", "추워지고", "있습니다."})
  - 여기서는 string만 썻는데 strings 말고 다양한 attribute있음

- 인자 값들의 소스
  - @ValueSource
  - @NullSource, @EmptySource, @NullAndEmptySource
    - NullSource는 method의 param에 null을 넣어줌
    - EmptySource는 method의 param에 빈문자열 넣어줌
    - NullAndEmptySource 는 위 두개 합친 composed annotation. 즉 위 두개를 각각 붙인거랑 같음
      - > test 2번 추가되는것.
  - @EnumSource
  - @MethodSource
  - @CsvSource
    - 이거 쓰면 여러 인자를 넘겨줄수 잇음. valueSource는 method param 으로 한개만 사용 가능했었음
  - @CvsFileSource
  - @ArgumentSource

아래 예제를 보면 대강 사용법 알수 있음

```java
    // ValueSource로 int를 받을수 있따. 
    @ValueSource(ints = {10,20,30})
    void parameterizedTest2(Integer message) {
        System.out.println("message = " + message);
    }

    // ValueSource로 받은 값을 다른 type에 넣을수 있는데. 이떄 @ConvertWith 로 컨버터 준거 중요
    @ValueSource(ints = {10,20,30})
    void parameterizedTest3(@ConvertWith(StudyConverter.class) Study message) { // 이렇게 받으려면 컨버터가 필요함 ( spring 꺼 말고..)
        System.out.println("message = " + message.getValue());
    }

    static class StudyConverter extends SimpleArgumentConverter {

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }


    //CsvSource 로는 여러 인자를 받을수 있는데 아래와 같은 형태로 인자들을 전달하는거 주의
    // {"인자1, 인자2","인자3, 인자3"} => 인자1,2가 한세트, 인자3,4가 한세트
    // 구분은 ,로하는데 구분자 변경도 가능하다함. 
    @DisplayName("스터디 만들기")
    @ParameterizedTest( name = "{index} {displayName} message = {0}") // test method의 param을 {0}으로 받음.
    @CsvSource({"10, '자바 테스트'", "20, 스터디 "})   // 두개의 인자를 넘겨줄수 있음 CsvSource이용하면.
    void parameterizedTest4(Integer limit, String name) {
        System.out.println("message = " + new Study(limit,name).toString());
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest( name = "{index} {displayName} message = {0}") // test method의 param을 {0}으로 받음.
    @CsvSource({"10, '자바 테스트'", "20, 스터디 "})   // 두개의 인자를 넘겨줄수 있음 CsvSource이용하면.
    void parameterizedTest5(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        System.out.println("message = " + study.toString());
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest( name = "{index} {displayName} message = {0}") // test method의 param을 {0}으로 받음.
    @CsvSource({"10, '자바 테스트'", "20, 스터디 "})   // 두개의 인자를 넘겨줄수 있음 CsvSource이용하면.
    void parameterizedTest6(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println("message = " + study.toString());
    }

    // 반드시 public class이거나
    // static inner class여야함
    static class StudyAggregator implements ArgumentsAggregator {

        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(1));
        }
    }


```

- 인자 값 타입 변환
  - 암묵적인 타입 변환
    - 아래 참고
    - <https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-argument-conversion-implicit>
  - 명시적인 타입 변환
    - > 위 예제 ValueSource에서 확인함
    - SimpleArgumentConverter 상속 받은 구현체 제공
    - @ConvertWith

- 인자 값 조합
  - > CsvSource 예제에서 확인함
  - ArgumentsAccessor
  - 커스텀 Accessor
    - ArgumentsAggregator 인터페이스 구현
    - @AggregateWith

크게 중요한 장은 아닌듯.

## JUnit 5 테스트 인스턴스

그 test 짤때 test method가 있는 class를 만들게 되는데, 이거 instance 만들어 지는 전략을 소개함.
기본적으로는 method마다 class의 인스턴스가 만들어짐.
즉 class에 field가 있을때 test method안에서 해당 field의 값을 조작해도, 다음 method에서는 class의 인스턴스가
새로 생성되므로.. field은 초기화된 값 그대로 나오게됨.

- JUnit은 테스트 메소드 마다 테스트 인스턴스를 새로 만든다.
  - 이것이 기본 전략.
  - 테스트 메소드를 독립적으로 실행하여 예상치 못한 부작용을 방지하기 위함이다.
    - > 즉 테스트간 의존성을 없애기 위함. 테스트간 공유하는 변수가 있거나.. 하면 테스트 순서에 따라 문제가 나올수도있고..
    - > 테스트는 순서없이 실행됨.
    - > 물론 JUnit 5에서는 선언된 순서대로 test가 실행되기는 하는데.. 매번 그런건 또 아님. 👍
  - **이 전략을 JUnit 5에서 변경할 수 있다** 👍
    - > 즉 이거는 JUnit 5에서만 가능

- `@TestInstance(TestInstance.Lifecycle.PER_CLASS)`
  - 이걸 test class 위에 붙여주면된다.
  - 테스트 클래스당 인스턴스를 하나만 만들어 사용한다.
  - 장점.
    - test마다 class 인스턴스 만들지 않아도 되니.. 약간의 성능향상
    - 일부 test 제약을 완화할수 있음
    - 예를 들면
      - @BeforeAll과 @AfterAll은 원래 static method여야하는데, 이제는 static이 아니어도 됨
      - 즉  @BeforeAll과 @AfterAll을 인스턴스 메소드 또는 인터페이스에 정의한 default 메소드로 정의할 수도 있다
    - 경우에 따라, 테스트 간에 공유하는 모든 상태를 @BeforeEach 또는 @AfterEach에서 초기화 할 필요가 있다.
  
즉 test class의 인스턴스를 하나만 만들어서 쓰는 방법인데, 이게 유용한 케이스가 하나 있다고함
그건 아래 테스트 순서에 나옴

## JUnit 5 테스트 순서

내부적으로 정해진 순서가 있기는 함.
  
실행할 테스트 메소드 특정한 순서에 의해 실행되지만 어떻게 그 순서를 정하는지는 의도적으로 분명히 하지 않는다.  
(테스트 인스턴스를 테스트 마다 새로 만드는 것과 같은 이유, 즉 test method간 디펜던시 없게 하려고..)  
  
경우에 따라, 특정 순서대로 테스트를 실행하고 싶을 때도 있다.  
> integration test나, 시나리오 test할때나.. (회원이 가입하고.. 로그인하고..) => 즉 use case test
> 즉 test간에 의존성도 있고 status도 있고 data도 공유하고.. 이럴려면 test class는 한번만 만들어 져야함 👍

그 경우에는 테스트 메소드를 원하는 순서에 따라 실행하도록  
`@TestInstance(Lifecycle.PER_CLASS)`와 함께 @TestMethodOrder를 사용할 수 있다.  

- `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`
  - 이걸 `@TestInstance(Lifecycle.PER_CLASS)` 와 함꼐 test class에 붙여줌
    - > 물론 반드시 같이 써야 하는건 아니고. 위에 예시를 든 상황들에서는 같이 쓰면 좋다는것!!
    - > 근데 생각해보면 class 인스턴스가 method마다 만들어지는 상황에서 굳이 test 순서를 맞추는게 의미가 없을거 같다함
    - > 즉, class 인스턴스가 하나인 상황에서.. 시나리오 test 같은걸 준비할떄 test 순서를 맞추는게 의미 있음
  - 이떄 attribute의 value는 MethodOrder 구현체의 class를 주면된다
  - 기본적으로 아래 세가지가 준비 되어 있음
    - Alphanumeric
    - OrderAnnoation
    - Random

- `@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`
  - OrderAnnotation 을 이용한 경우
  - Test method에 `@Order` 어노테이션을 보고 test를 순서대로 실행해줌
  - 주의!
    - @Order는 junit꺼 붙여야함. spring꺼 말고.!

- JUnit4 용 순서 맞추기
  - <https://github.com/junit-team/junit4/wiki/Test-execution-order>
  - from version 4.11 부터 아래 어노테이션 사용
  - @FixMethodOrder(MethodSorters.NAME_ASCENDING)

## JUnit 5 junit-platform.properties

JUnit을 설정하는 기능, JUnit5에서 파일로 제어할수 있도록 제공함

- JUnit 설정 파일로, 클래스패스 루트 (src/test/resources/)에 넣어두면 적용된다.
  - 중요! 👍
    - 해당 resources를 만들었는데 intellij에서 test resources로 인식하지 않으면 이걸 classpath로 사용안함
    - 즉 적용이안됨
    - 그래서 test를 실행할떄 resources directory를 classpath로 사용하려면
    - intellij project sturucture의 module에 가서 해당 package를 test resouce로 등록해줘야함
    - 또는 오른쪽 클릭 mark directory as로 세팅.
    - > 이거 봤었음

- 테스트 인스턴스 라이프사이클 설정
  - junit.jupiter.testinstance.lifecycle.default = per_class
  - > 어노테이션으로 학습했던거.. 이거 file로 세팅하면 모든 test전체 다걸림

- 확장팩 자동 감지 기능
  - junit.jupiter.extensions.autodetection.enabled = true
  - > 다음 강좌 주제.

- @Disabled 무시하고 실행하기
  - junit.jupiter.conditions.deactivate = org.junit.*DisabledCondition
  - > org.junit.~ package 아래있는 DisabledCondition class를 무시 하겠다는것.
  - > 만약 @DisabledOnJre같은거 무시하고 실행하려면?
  - > DisabledOnJreCondition 를 DisabledCondition 대신 쓰면됨.

- 테스트 이름 표기 전략 설정
  - junit.jupiter.displayname.generator.default = org.junit.jupiter.api.DisplayNameGenerator$ReplaceUnderscores
  - > 앞서 살펴봤었음 annotation으로..
  - 물론 @DisplayName을 붙였다면 이거대로 이름이 출력됨. 이게 우선순위가 가장 높음 👍

이거 말고도 한가지 더 세팅할수 있는데, 그건 실험적인 기능이라서 강좌에서는 다루지 않음

## JUnit 5 확장 모델

- JUnit 4의 확장 모델(확장하는 방법)은 아래 세가지 였다
  - @RunWith(Runner), TestRule, MethodRule
- JUnit 5의 확장 모델은 단 하나, Extension

- 확장팩 등록 방법. 크게 아래 세가지가 있음
  - > 여기서는 두가지만 볼꺼고, 단순한 사용방법으로 예시를 만들어봄
  - 선언적인 등록 @ExtendWith
  - 프로그래밍 등록 @RegisterExtension
  - 자동 등록 자바 ServiceLoader 이용 
    - > 이거는 가이드 문서 참조하라고 함

- 확장 모델 만드는 가이드
  - <https://junit.org/junit5/docs/current/user-guide/#extensions>
  - 상당히 내용이 많음.
  - 즉. 확장 모델이란
    - test instance를 만들고, parameter resolution( param di 하는거. .등). test lifecycle callback 호출
    - 등등 테스트를 어떻게 진행할지 전반에 걸쳐 세팅하는것

- 예시

```java

  //@ExtendWith(FindSlowTestExtension.class)  // extension 사용하는 첫번째 방법. 선언적 방법
                                              // 단점. 이방법으로는 FindSlowTestExtension 의 인스턴스 생성을 제어할수 없다.
                                              // 즉, 만약 test마다 FindSlowTestExtension의 Threshold를 다르게 주고 싶다면?
                                              //FindSlowTestExtension 의 생성자로 Threshold를 줘야하는데.. 이걸 할수가 없음
  @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
  class StudyTest {

      // 아래 처럼만 하면 extension이 등록됨.
      @RegisterExtension
      static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000L);


      @Test
      @DisplayName("Slow test")
      void slow_test() throws InterruptedException {
          Thread.sleep(1005L);

          System.out.println("extension 확인을 위한 느린 테스트");
      }
  }

```

```java
// lifecyle callback 두개 구현
// 오래 걸리는 test case를 찾아서 SlowTest 어노테이션을 사용하도록 권장하는 기능
public class FindSlowTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

//    private static final long THRESHOLD = 1000L; // 1초

    private long THRESHOLD;

    public FindSlowTestExtension(long THRESHOLD) {
        this.THRESHOLD = THRESHOLD;
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        String testClassName = extensionContext.getRequiredTestClass().getName();
        String testMethodName = extensionContext.getRequiredTestMethod().getName();
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));

        store.put("START_TIME", System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {

        // 리플렉션임. 이걸로 SlowTest 어노테이션이 이미 붙은 Test는 무시하게함
        Method requiredTestMethod = extensionContext.getRequiredTestMethod();
        SlowTest annotation = requiredTestMethod.getAnnotation(SlowTest.class);

        String testClassName = extensionContext.getRequiredTestClass().getName();
        String testMethodName = extensionContext.getRequiredTestMethod().getName();
        ExtensionContext.Store store = extensionContext.getStore(ExtensionContext.Namespace.create(testClassName, testMethodName));

        long start_time = store.remove("START_TIME", long.class);
        long duratoin = System.currentTimeMillis() - start_time;

        if( duratoin > THRESHOLD && annotation == null) {
            System.out.printf("Please consider mark method [%s] with @SlowTest.", testMethodName);
        }

    }

}

```

## JUnit 5 마이그레이션

JUnit 5가 제공하는 JUnit 4 마이그레이션 

- 기본적으로 spring boot로 프로젝트를 만들면 vintage-engine이 빠진상태라고함
  - > 이게 있어야 JUnit 4 test들을 실행할수 잇음
  - 🥉 근데 spring boot 2.7.0으로 만드니 강사 화면이랑 다름
    - 즉 exclusions으로  vintage-engine 이 빠져 있어야하는데. 이미 해당 문구는 없음
    - 근데 dependency에도 vintage-engine이 들어 있찌는 않음

    ```xml
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>

    ```
    > 2.7.0에서 어떻게 세팅해야 할지 나중에 확인 필요

- JUnit 4로 작성한 test는 
  - JUnit 5의 Junit platform이 vintage 엔진을 통해 실행시켜 주는듯
  - `@Test` 도 jupiter꺼를 쓰면 안되고 JUnit 꺼를 쓰면 이게 JUnit 4 꺼임
  
- JUnit5에서 Vintage 엔진 추가헤서 JUnit4 test 돌릴땐 제약사항이 잇음
  - @Rule은 기본적으로 지원하지 않음
  - junit-jupiter-migrationsupport 모듈을 추가해서, 이게 제공하는
  - @EnableRuleMigrationSupport를 사용하면 다음 타입의 Rule을 지원한다.
    - ExternalResource
    - Verifier
    - ExpectedException


- Junit4 -> Junit5
  - @Category(Class) -> @Tag(String)
  - @RunWith, @Rule, @ClassRule -> @ExtendWith, @RegisterExtension
  - @Ignore -> @Disabled
  - @Before, @After -> @BeforeEach, @AfterEach,
  - @BeforeClass, @AfterClass -> @BeforeAll, @AfterAll


> 이 양반 Rest 강좌.. 괜찮을수 있을거 같음. Test Case도 짯네. 보니까

## JUnit 5 연습 문제

학습했던 내용 정리 하는 시간

## 기타 추가 내용

- Befor 류에 fail이 나왔을시?
  - https://stackoverflow.com/questions/61010940/junit5-react-to-before-methods-failure

- jsonNode 비교
  - https://www.baeldung.com/jackson-compare-two-json-objects
  - 고민 포인트
    - assertj에서 collection객체를 비교해주는게 있을까?
    - 또는 jsonNode를 비교할수 잇을까?
  - 해결
    - 결국 두 객체가 equal을 어떻게 구현했는지가 관련
      - > effective java에서 collection들은 eqauls를 value 비교.. 로 구현했다 한거 같기도.. -> 검증 필요

- test suite 만들기
  - https://www.softwaretestinghelp.com/junit-test-suite/#Creating_A_Test_Suite_grouping_Multiple_Test_Classes