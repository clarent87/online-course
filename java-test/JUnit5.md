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

## JUnit 5 조건에 따라 테스트 실행하기

## JUnit 5 태깅과 필터링

## JUnit 5 커스텀 태그

## JUnit 5 테스트 반복하기 1부

## JUnit 5 테스트 반복하기 2부

## JUnit 5 테스트 인스턴스

## JUnit 5 테스트 순서

## JUnit 5 junit-platform.properties

## JUnit 5 확장 모델

## JUnit 5 마이그레이션

## JUnit 5 연습 문제
