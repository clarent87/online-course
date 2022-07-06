# Mockito

## Mockito 소개

- 현재 최신 버전 3.1.0
- 단위테스트 고찰
  - <https://martinfowler.com/bliki/UnitTest.html>
  - 단위라는 것은 하나의 행동이 단위가 될수도 있음
    - 즉, 하나의 행동을 위해 구현된 의존된 코드들이 다 test 가 되어도 단위 테스트라 부를수도 잇음
    - > 뭐 대충 이렇게 생각하는 사람들도 있다함. 이부분은 논란이 있을수도 있음
- 강사 의견
  - 제어가 불가능한 외부 서비스 정도만 mocking하면 될거 같다함
  - 즉, 이미 code에 만들어둔 class들을 굳이 mocking해서 unit test를 만드는게 의미가 있나.. 싶다고함.
  - > 음. db나 mqtt, rest같은것은 mocking하는게 맞겠지?
  - > 추가적으로 외부 서비스도 test bad를 제공하는 경우는 거기에 대고 test code만든다함. -> 이거 system test? 그런개념인가?
  - > Unit test는 아닐거 같음.
  - > Unit test가 기존에 나온 책에 있었는데.. clean code? tdd? 거기서는 뭐라고 나왔었지?

## Mockito 시작하기

- spring boot 쓰면 기본으로 들어옴
- 다음 세 가지만 알면 Mock을 활용한 테스트를 쉽게 작성할 수 있다.
  - Mock을 만드는 방법
  - Mock이 어떻게 동작해야 하는지 관리하는 방법
  - Mock의 행동을 검증하는 방법

## Mock 객체 만들기

코드에 MemberService, StudyRepository 가 interface만 있는채로 테스트를 작성하는 예제임
> 뻔한 예제긴 함

Mockito로 Mock 객체를 일단 만드는 방법은 아래 세가지

```java

// Method에서 Mockito의 static method인 mock을 이용한다. 
// 이때 param은 type을 넣어주면 된다. (interface나 class)
// 원리는 그냥 생으로 interface를 instance화 한거랑 동일
MemberService memberService = mock(MemberService.class);
StudyRepository studyRepository = mock(StudyRepository.class);

// 또는 아래 처럼 Mock Extension을 이용
// ExtendWith는 Junit5 어노테이션
// 앞서 Junit5 extension은 만들어 봤었다. ( test method 수행시간 보고 slowTest 어노테이션 붙여라 경고 주려고)
// MockitoExtension 은 @Mock 어노테이션을 처리해줌
@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    // 이렇게 field로 주입 받아서 test method들에서 활용해도 되고
    @Mock MemberService memberService;
    @Mock StudyRepository studyRepository;

    // 아래 처럼 test method의 param으로 받아서 해당 test 에서만 써도 된다.
    @Test
    void createStudyService(@Mock MemberService memberService,
                            @Mock StudyRepository studyRepository) {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService); // null이면 error
    }
}

```

## Mock 객체 stubbing

stubbing 이란 mock 객체의 행동을 조작하는!. 앞선 챕터에서는 Mock 객체만 만들었지 Mock 객체의 method 행동을 제어하지는
않았음
  
- Mock 객체 기본 동작
  - Null을 리턴한다. (Optional 타입은 Optional.empty 리턴)
  - Primitive 타입은 기본 Primitive 값.
  - 콜렉션은 비어있는 콜렉션.
  - Void 메소드는 예외를 던지지 않고 아무런 일도 발생하지 않는다.

- stubbing 기본

  ```java
  // 모키토 스터빙은 아래 처럼 진행
  when(memberService.findById(any())) // memberService.findById(any()) 가 호출이 되면.
          .thenReturn(Optional.of(member)) // Optional.of(member)를 리턴 해라

  ```

  - 여기서 any()는 argument matcher임. 이경우는 findById에 어떤 param이 오던 member optional이 return 됨
    - 참조
      - <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#2>
      - <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/ArgumentMatchers.html>

- **Void 메소드**에 대해 특정 매개변수를 받거나 호출된 경우 예외를 발생 시킬 수 있다.
  - Subbing void methods with exceptions
  - > 당연한 얘긴데 void method의 경우 thenReturn같은게 있을수가 없음.

  ```java
      // 2. void method 호출시 예외 던지는 스터빙
      // 모키토 문서 Stubbing void methods with exceptions 에서 보면 void method는 특수하게도 예외 스터빙은 아래처럼 해야함
      doThrow(new IllegalArgumentException()).when(memberService).validate(1L);
      assertThrows(RuntimeException.class, () -> {
          memberService.validate(1L);
      });

  ```

- 메소드가 동일한 매개변수로 여러번 호출될 때 각기 다르게 행동호도록 조작할 수도 있다.
  - Stubbing consecutive calls

  ```java
      // 3. 메소드가 동일한 매개변수로 여러번 호출될 때 각기 다르게 행동호도록 조작할 수도 있다.
      // Stubbing consecutive calls
      when(memberService.findById(any()))
              .thenReturn(Optional.of(member)) //  findById 첫번째 호출시
              .thenThrow(new RuntimeException()) //  findById 두번째 호출시
              .thenReturn(Optional.empty()); // findById 세번쨰 호출시


      Optional<Member> byId = memberService.findById(1L); // findById 첫번째 호출시
      assertEquals("keesun@email.com", byId.get().getEmail());

      assertThrows(RuntimeException.class, () -> {
          memberService.findById(2L); //  findById 두번째 호출시
      });

      assertEquals(Optional.empty(), memberService.findById(3L)); // findById 세번쨰 호출시

  ```

- answer인터페이스를 이용해서 argument에 따라 return 결과를 동적으로 제어 가능
  - <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#21>

## Mock 객체 stubbing 연습문제

연습문제 풀이인데, 간단함. 아 굳이 풀진 안았다.

## Mock 객체 확인

> 앞서 연습문제 예제/해답 가져왔음

Mock 객체가 어떻게 사용이 됐는지 확인할 수 있다. ( mock 객체에 어떤일이 일어나는지 확인)

- 특정 메소드가 특정 매개변수로 몇번 호출 되었는지, 최소 한번은 호출 됐는지, 전혀 호출되지 않았는지
  - Verifying exact number of invocations
- 어떤 순서대로 호출했는지
  - Verification in order
- 특정 시간 이내에 호출됐는지
  - Verification with timeout
  - > 이건 따로 해보진 않음. 근데 이거 쓸바에는 junit의 assertTimeout 쓰는게 좋다는듯.
- 특정 시점 이후에 아무 일도 벌어지지 않았는지
  - Finding redundant invocations
  
위 내용의 예시는 아래와 같다.

```java
  //  studyService.createNewStudy 호출시 내부적을
  //  memberService.notify(newstudy); 가 호출되는데, 이게 호출이 잘 됬는지 알길이 없음
  //  memberService 는 mocking했고..
  //  이럴때 아래와 같은 verify를 써서 mock의 동작을 확인 가능 ( matcher사용 가능 )
  // 특정 시간안에 호출이 되야하는 게 있는경우 mockito의 timeout을 쓸수 있는데, 이럴 바에는 junit timeout을 쓰는게 낫다
  verify(memberService, times(1)).notify(study); // notify가 1번 study 매개변수로 호출됬어야 한다.
  verify(memberService, times(1)).notify(member);
  verify(memberService, never()).validate(any()); // validate 는 한번도 호출되지 않았어야한다.

  // 만약 notify 함수가 study로 한번 member로 한번 순서대로 호출되었는지 검증하려면?
  // 이건 쫌 너무한 test 같다고는함. ( 순서까지 확인하는거.. )
  // > 위쪽에서 verify한건 호출된 횟수 파악.하는거고 순서 확인에 영향을 주지는 않네.
  InOrder inOrder = inOrder(memberService);
  inOrder.verify(memberService).notify(study); // study로 먼저 호출되고
  inOrder.verify(memberService).notify(member);// member로 호출되어야한다.

  verifyNoInteractions(memberService); // 이거 호출된 이후로 더이상 memberService mock에 상호작용이 있어서는 안된다.

```

## BDD 스타일  Mockito api 👍

BDD 스타일을 Mockito도 지원을 한다.  
> 대충 BDD에 대한 설명 지원하는 framework들이 있따.. 뭐 이런 설명이 있긴했는데, 크게 도움은 안됨

아래 예시를 보면 그냥 Mockito를 BDD 로 변하는거 있음. 이거 보는게 좋음

```java
    // BDD를 따르려면 test 이름도 should~~ 로 되어야함. (https://matheus.ro/2017/09/24/unit-test-naming-convention/)
    // 근데 그냥 display name만 잘 써줘도 될거 같다함.
    @Test
    void createNewStudyBDD() {
        //Givne
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("keesun@email.com");

        Study study = new Study(10, "테스트");

        // stubbing하는 부분은 BDD에 따르면 given에 해당하는데,, api이름이 맞지 않다..
        // 그래서 BDD Mockito을 이용
//        when(memberService.findById(1L)).thenReturn(Optional.of(member));
//        when(studyRepository.save(study)).thenReturn(study);

        // 위 코드를 아래처럼 바꿀수 있다. (BDDMockito package 이용)
        // 즉 given 절에 쓰는 방식 👍
        given(memberService.findById(1L)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        //When
        studyService.createNewStudy(1L, study);

        //Then
        assertEquals(member, study.getOwner()); // 쥬피터꺼

        // 아래도 BDD style은 아님.. 그래서 BDDMockito 의 API로 변경하면..
//        verify(memberService, times(1)).notify(study);
        // 이게 BDD 스타일. should 안에는 아무것도 안넣을 수도 있음
        // then절에 쓰는 방식 👍
        then(memberService).should( times(1)).notify(study);

        // 이것도 BDD로 변경해보면
//        verifyNoInteractions(memberService);
        then(memberService).shouldHaveNoMoreInteractions();

    }

```

- 참고
  - <https://javadoc.io/static/org.mockito/mockito-core/3.2.0/org/mockito/BDDMockito.html>
  - <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#BDD_behavior_verification>

## Mockito 연습문제

정리.  
> 문제 풀지는 않았음

- Q.
  static method mocking을 하려고 알아봤는데 mockito로는 안되는 것 같고
  Powermock이 static method mocking이 가능하지만
  junit5에서는 powermock 지원이 안된다고 하네요..
  좋은 방법이 있을지 문의드립니다.

- A
  - JMockit이라는걸 써보시죠.
  - <https://www.baeldung.com/jmockit-static-method>

## 내가 추가한것

### spy

mock과 spy는 사용법이 약간 다름.

```java
@Mock(name = "database") private ArticleDatabase dbMock; // 보면 mock은 객체 생성을 하지 않음.
@Spy private UserProvider userProvider = new ConsumerUserProvider(); // spy는 real 객체를 줘야함.

```

### @InjectMocks and @Mock(name)

- 참조
  <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/InjectMocks.html>

inject하는 전략이 잇음

```java

   public class ArticleManagerTest extends SampleBaseTestCase {

       @Mock private ArticleCalculator calculator;
       @Mock(name = "database") private ArticleDatabase dbMock; // note the mock name attribute
       @Spy private UserProvider userProvider = new ConsumerUserProvider();

       @InjectMocks private ArticleManager manager;

       @Test public void shouldDoSomething() {
           manager.initiateArticle();
           verify(database).addListener(any(ArticleListener.class));
       }
   }

  // 1. 생성자 있을대 주입 됨
  public class ArticleManager {
       ArticleManager(ArticleCalculator calculator, ArticleDatabase database) {
           // parameterized constructor
       }
   }

  // 2. Field 인젝션 가능. 모키토가 reflection이용
  public class ArticleManager {
       private ArticleDatabase database;
       private ArticleCalculator calculator;
   }

   // 3. 세터 주입도 가능
   public class ArticleManager {
       // no-arg constructor
       ArticleManager() {  }

       // setter
       void setDatabase(ArticleDatabase database) { }

       // setter
       void setCalculator(ArticleCalculator calculator) { }
   }

   // 이경우는 inject가 안됨. 
   public class ArticleManager {
       private ArticleDatabase database;
       private ArticleCalculator calculator;

       ArticleManager(ArticleObserver observer, boolean flag) {
           // observer is not declared in the test above.
           // flag is not mockable anyway
       }
   }
 

```

- `@Mock(name = "database")`
  - mock inject될때 이 mock은 database란 이름의 변수에 inject됨
    - > 이거 mockBean의 내용과 유사하다.

### Captor

<https://site.mockito.org/javadoc/current/org/mockito/ArgumentCaptor.html>
<https://www.baeldung.com/mockito-argumentcaptor>

`Mockito.verify(platform).deliver(emailCaptor.capture());` 처럼 verify할때  param을 캡쳐해서 검사하는데 사용 가능

기본 사용법

```java
   ArgumentCaptor<Person> argument = ArgumentCaptor.forClass(Person.class);
   verify(mock).doSomething(argument.capture());
   assertEquals("John", argument.getValue().getName());

    //capturing varargs:
   ArgumentCaptor<Person> varArgs = ArgumentCaptor.forClass(Person.class);
   verify(mock).varArgMethod(varArgs.capture());
   List expected = asList(new Person("John"), new Person("Jane"));
   assertEquals(expected, varArgs.getAllValues());

```

`@Captor` 는 아래와 같이 사용하는데, warnings related capturing complex generic types 를 회피하는데 씀

```java
 public class Test{

    @Captor ArgumentCaptor<AsyncCallback<Foo>> captor; // 복잡한 genric type을 captor..

    @Before
    public void init(){
       MockitoAnnotations.initMocks(this);
    }

    @Test public void shouldDoSomethingUseful() {
       //...
       verify(mock).doStuff(captor.capture());
       assertEquals("foo", captor.getValue());
    }
 }

```

### abstract class도 모킹이 가능

- 참조
  - <https://site.mockito.org/javadoc/current/org/mockito/Mockito.html#30>
  - Spying or mocking abstract classes (Since 1.10.12)

Previously, spying was only possible on instances of objects

```java
 //convenience API, new overloaded spy() method:
 SomeAbstract spy = spy(SomeAbstract.class);

 //Robust API, via settings builder:
 OtherAbstract spy = mock(OtherAbstract.class, withSettings()
    .useConstructor().defaultAnswer(CALLS_REAL_METHODS));

 //Mocking a non-static inner abstract class:
 InnerAbstract spy = mock(InnerAbstract.class, withSettings()
    .useConstructor().outerInstance(outerInstance).defaultAnswer(CALLS_REAL_METHODS));

```

For more information please see MockSettings.useConstructor().

### interface mocking

- <https://www.baeldung.com/java-spring-mockito-mock-mockbean>

기본적으로 모킹은 class, interface에 대해 가능한게 기본임

## 기타

- void method의 경우 사용하는 api가 쫌 다름
  - <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#do_family_methods_stubs>
  - BDD일때도 어느정도 영향있을듯?

- beforeall에서는 stubbing 안되나봄. 👍
  - mockitoExtesion이 beforeall에 대해 구현이 안되어 있다네.
  - <https://stackoverflow.com/questions/65543399/mockito-does-not-initialize-mock-in-test-running-with-junit-5-in-beforeall-anno>
