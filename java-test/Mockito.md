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

## Mock 객체 stubbing 연습문제

## Mock 객체 확인

## BDD 스타일  Mockito api

## Mockito 연습문제
