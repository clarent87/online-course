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

## Mock 객체 stubbing

## Mock 객체 stubbing 연습문제

## Mock 객체 확인

## BDD 스타일  Mockito api

## Mockito 연습문제
