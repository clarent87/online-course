# BDD 스타일 테스트

> Write BDD Unit Tests with BDDMockito and AssertJ 글 정리

## 기본 정보

- 기본적으로 Unit 테스트를 위한 방법론
- 예시는 JUnit4임

- <https://thepracticaldeveloper.com/write-bdd-unit-tests-with-bddmockito-and-assertj/>
- Mockito와 BDDMockito는 뭐가 다를까?
  - <https://velog.io/@lxxjn0/Mockito%EC%99%80-BDDMockito%EB%8A%94-%EB%AD%90%EA%B0%80-%EB%8B%A4%EB%A5%BC%EA%B9%8C> 👍

## BDD의 장점 요약

남의 코드는 테스트 코드로 기능파악하는게 좋은데, 테스트 코드가 지저분하면 이렇게 하기 힘듬  
그래서 잘 이해할수 있도록 짜는게 BDD 스타일  

- Given-When-Then: Tests that a human can read
  - Given-When-Then 으로 테스트 시나리오 만들어야한다. 실제 코드 만들기 전에..  -> BDD\
    - > 요구사항 뽑기도 좋다네.

- 장점
  - Much better test readability, which leads to less time required to maintain your unit tests.
  - Your test code is your documentation

## 기본 정보 정리

- POJO 및 도메인 모델
  - > POJO는 그냥 바닐라 java로 만든 data class라고 보면될듯.
  - > 물론 lombok 쓴 data class도 POJO일듯
  - > 도메인 모델은 그냥 data class.. 라고 보면 될듯. db에 저장할. -> 난 DTO라고 표현한거 같은데.. 사실. DTO는 아니지..

- 예시에서는 외부 서비스를 위한 interface가 두개가 존재함
  - > 즉 외부 서비스가 연결이 안되었다는 가정에서 mock을 통한 test를 작성하는 예제..

- Given When Then 설명
  - Given : 테스트 케이스 셋업, 가정, pre조건, 요구사항 같은거 작성하는부붐
  - when : 테스트 진행 ( 보통 code의 한 두라인을 커버하는 test)
  - then : 결과 처리/검증
  - Given this preconditions, When this action happens, Then these results should be obtained

- Advantages of using BDD’s Given-When-Then
  - 개발자나 business analysts 가 읽기 쉬움. test 코드를...
    - business analysts가 Cucumber로 작성한 test 보고 작성하기도 편하다.. 뭐대충. 그런?
  - test를 전부 이방식으로 만들면, 다른 사람들이 읽기 편하다.. 다 똑같은 컨벤션이라서..

## BDDMockito 와 given

- BDDMockito 의존성 추가 필요?
  - 이거 단순 class 파일임. Mockito 라이브러리에 있는.. 따라서 별다른 의존성은 필요하지 않음

- BDDMockito 랑 Standard Mockito 차이
  - > 아래 내용은 BDD given.. 만 소개한것
  - Standard Mockito의 syntax
    - `when(methodCall).then(doSomething)`
  - BDDMockito 의 syntax
    - `given(methodCall).will(doSomething)`

- BDDMockito 의 willReturn, willAnswer 차이 👍
  - willAnswer는 mock method의 param을 이용해서 retsult를 만들때 사용
  - 이거 일반 Mockito의 doAnswer과 같음
  - > 쫌 찾아보니 void method의 경우 when 대신 do~를 써야하나봄.  BDD아니더라도..
  - > <https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#do_family_methods_stubs>

  ```java
  given(populationService.forCity(MALAGA))
    .willReturn(Optional.of(MALAGA_POPULATION));
  given(cityRepository.save(any(City.class)))
    .willAnswer(answer -> ((City) answer.getArgument(0)).copyWithId(randomLong()));

  ```

- Where is the `When` in Mockito BDD?
  - when절용 BDD 모키토는 당연히 없음. 그냥 알아서 target api호출하고 위에 `//when` 붙여준다.

## AspectJ와 Then 절

- `then` 절은 어떻게??
  - assertions는 aspectJ로 구현
  - > 물론 then 용 BDD모키토 있음. 이건 mock의 행동을 검증하기 위함. 기본 mockito에도 있는데, BDD 스타일도 제공한다는것
  
- AssertJ vs. JUnit assertions
  - JUnit 은 읽기가 어렵다. actual이랑 expect value를 혼돈해서 쓰거나.. 등
  - AssertJ 중요 장점 두가지
    - 많은 기능 제공. JUnit으로는 assertions을 위해 result를 가동하는 일이 빈번 했나봄
    - 읽기 편함
      - `assertEquals("Check that City ID is set when stored", inputCity.getId(), actualCity.getId())`
      - `assertThat(actualCity.getId()).as("Check that City ID is set when stored").isEqualTo(inputCity.getId())`
      - > 차이를 봐라

- AssertJ 는 `assertThat`을 제공하는데 BDD alias인 `then` 도 제공함 👍
  - > 확인함.

### AssertJ 기본

바닐라 AssertJ는 기본적으로 `Assertions.assertThat(actual)` 를 이용함.
근데 BDD-friendly alias (like BDDMockito has) 도 제공 `BDDAssertions.then(actual)`  
동작은 같음  
> 이걸로 then절을 채우면 된다.
  
예제  
  
```java
// Then
final City expectedCity = new City(null, MALAGA, MALAGA_POPULATION);
then(actualCity.getId())
  .as("Check that City ID is set when stored.")
  .isNotNull();
then(actualCity)
  .as("Check that City name is correct and city population is filled in.")
  .isEqualToIgnoringGivenFields(expectedCity, "id");  // expectedCity, actualCity 를 비교

```
  
assertions의 description은 ` as(description) ` 로 작성.  
이거는 assert가 fail났을때 error message를 작성하는 것.  
동시에 이건 test documentation이기도함  
  
기타 다른 useful method도 제공. 이를테면 위 예시의 `isEqualToIgnoringGivenFields`  
이거 같은 경우  expectedCity, actualCity 를 비교하는데 method에 전달된 id는 빼고 equality를 비교해줌
( 즉  AssertJ will ignore your equals() implementation )  
  
### AssertJ에서 제공하는 유용한 assertions

일단 AssertJ의 장점은 실계 예제가 가득한
<https://joel-costigliola.github.io/assertj/assertj-core-features-highlight.html> 이다.  
<https://assertj.github.io/doc/>
> 대충 보니 첫페이지만 잘봐도 될듯.
  
테스트 케이스 짤때, 시나리오에 맞는 assertion이 있는지 찾을때 쓰면 좋음. 대부분 있음
  
일단 full version test 예시는 다음과 같다.  
아래에서는 여기서 쓰인 useful assertion들을 소개 함

```java
@RunWith(MockitoJUnitRunner.class)
public class CityServiceTest {

  @Mock
  private CityRepository cityRepository;

  @Mock
  private PopulationService populationService;

  private CityService cityService;
  private static final String MALAGA = "Malaga";
  private static final String AMSTERDAM = "Amsterdam";
  private static final String BARCELONA = "Barcelona";
  private static final int MALAGA_POPULATION = 569_000;
  private static final int BARCELONA_POPULATION = 1_609_000;

  @Before
  public void setup() {
    cityService = new CityService(cityRepository, populationService);
    Assertions.useRepresentation(new CityRepresentation());
  }

  @Test
  public void createCity() {
    // Given
    final City inputCity = new City(null, MALAGA, null);
    given(populationService.forCity(MALAGA))
      .willReturn(Optional.of(MALAGA_POPULATION));
    given(cityRepository.save(any(City.class)))
      .willAnswer(answer -> ((City) answer.getArgument(0)).copyWithId(randomLong()));

    // When
    final City actualCity = cityService.enrichAndCreateCity(inputCity);

    // Then
    final City expectedCity = new City(null, MALAGA, MALAGA_POPULATION);
    then(actualCity.getId())
      .as("Check that City ID is set when stored.")
      .isNotNull();
    then(actualCity)
      .as("Check that City name is correct and city population is filled in.")
      .isEqualToIgnoringGivenFields(expectedCity, "id");
  }

  @Test
  public void createCityWithIdThrowsException() {
    // Given
    final City inputCity = new City(1L, MALAGA, null);

    // When
    final Throwable throwable = catchThrowable(() -> cityService.enrichAndCreateCity(inputCity));

    // Then
    then(throwable).as("An IAE should be thrown if a city with ID is passed")
      .isInstanceOf(IllegalArgumentException.class)
      .as("Check that message contains the city name")
      .hasMessageContaining(inputCity.getName());
  }

  @Test
  public void getCities() {
    // Given
    final City malaga = new City(1L, MALAGA, MALAGA_POPULATION);
    // let's say the service did not work for Amsterdam so it's stored without population...
    final City amsterdam = new City(2L, AMSTERDAM, null);
    final City barcelona = new City(3L, BARCELONA, BARCELONA_POPULATION);
    given(cityRepository.getAllCities()).willReturn(List.of(malaga, amsterdam, barcelona));

    // When
    final List<City> cities = cityService.getAllValidCities();

    // Then
    SoftAssertions.assertSoftly(softly -> {
      softly.assertThat(cities)
        .as("Should contain only two cities")
        .hasSize(2);
      softly.assertThat(cities).extracting("population")
        .as("Should not contain null populations")
        .doesNotContainNull();
      softly.assertThat(cities).extracting("name")
        .as("Should contain names in alphabetical order")
        .containsSequence(BARCELONA, MALAGA);
    });
  }

  private long randomLong() {
    return ThreadLocalRandom.current().nextLong(1000L);
  }
}

```

- Flexible equality assertions
  - `isEqualToIgnoringFields` 앞서 보였듯. 유연하게 비교하는 assertion들이 몇개 더 있음
    - > 객체 비교할때 field에 값이 준비가 안된 case에 쓰는것들 인듯
    - `isEqualToComparingFieldByField`
      - 객체가 다를때 `isEqualTo` 로는 fail이 나는데, 이걸 쓰면 field 단위로 비교를 해줘서 true가 나옴
      - > `isEqualTo` 는 기본적으로 객체 주소 비굔가??
    - `isEqualToComparingFieldByFieldRecursively`
      - 위 method의 Recursively 버전
      - > ?
    - `isEqualToIgnoringNullFields`
      - null 필드 무시 하고 비교 해주는거 같은데, 이거 잘 안쓰는게 좋다함
      - null나온 부분은 error가 나온 부분일테니 assert fail이 나게 하는게 좋은듯

- AssertJ Exception Assertions
  - `catchThrowable()` 이거 when에다 이용해서 함수의 exception을 받을수 있음
  - 그리고 return으로 나오는 Throwable을 assert하면됨
    - > assertj doc에 매우 잘 나와 잇음

    ```java
        // When
        final Throwable throwable = catchThrowable(() -> cityService.enrichAndCreateCity(inputCity));

        // Then
        then(throwable).as("An IAE should be thrown if a city with ID is passed")
        .isInstanceOf(IllegalArgumentException.class)
        .as("Check that message contains the city name")
        .hasMessageContaining(inputCity.getName());

    ```

- Easy handling of Soft Assertions
  - 보통 assert가 fail나면 그아래는 전부 실행이되지 않는데,, 이러지 말고 하나 fail나도 나머지는 계속 실행 시키고 싶은 경우
  - `SoftAssertions` 를 이용하면 된다
    - 저자는 아래처럼 functional style을 좋아한다고함
    - 이렇게 쓰는거 말고 softly.assertThat 을 나열하고 마지막에 `softly.assertAll();` 을 호출하는 방식이 기본임
    - BDDSoftAssertions 도 있음
      - 근데이건 functional style은 불가 하나봄
      - 그리고 doc를 보니까 junit5용 extesion도 있고 그러네.. `softly.assertAll();`  호출 없앨려고..

    ```java

        // Then
        SoftAssertions.assertSoftly(softly -> {
        softly.assertThat(cities)
            .as("Should contain only two cities")
            .hasSize(2);
        softly.assertThat(cities).extracting("population")
            .as("Should not contain null populations")
            .doesNotContainNull();
        softly.assertThat(cities).extracting("name")
            .as("Should contain names in alphabetical order")
            .containsSequence(BARCELONA, MALAGA);
        });

    ```

- Custom Object Representations
  - > 요거 신박하네.
  - 이를테면 test 하려는 객체가 (위 예시의 city 같은거)있다면
  - 대충 data class인데, 이게 만약 toString()을 지원하지 않는다면?
  - 물론 내 class면 toString 만들면 되는데, 만약 library에 빌드된 class면 toString 추가가 안되고.
  - assert 실패시 로그는 `City@7d12566b` 처럼 객체 주소 비스 무리 한게 나오게됨
  - 이거 대신 휴먼 리더블한 값을 전달할수 있도록,`StandardRepresentation`이거 이용해서 아래처럼 만들수 있음

```java
    // 아래처럼 class 만들고
    package com.thepracticaldeveloper.population;

    import org.assertj.core.presentation.StandardRepresentation;

    public class CityRepresentation extends StandardRepresentation {

        @Override
        protected String fallbackToStringOf(Object object) {
            if (object instanceof City) {
                final City city = (City) object;
                return "{id:" + city.getId() + ", name:" + city.getName() + ", population:" + city.getPopulation() + "}";
            }
            return super.fallbackToStringOf(object);
        }
    }

    // test class에 아래 셋업함
    @Before
    public void setup() {
        cityService = new CityService(cityRepository, populationService);
        Assertions.useRepresentation(new CityRepresentation());
    }

```
