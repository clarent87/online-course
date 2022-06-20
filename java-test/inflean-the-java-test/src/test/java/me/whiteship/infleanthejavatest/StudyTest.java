package me.whiteship.infleanthejavatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

    @Test
    @DisplayName("스터디 만들기")
    @Tag("fast")
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

    @Test
    @DisplayName("스터디 만들기")
    @Tag("slow")
    void create_new_study_again() {

        // 이게 만족해야, 그다음 assert들이 실행됨
        // 즉 만족을 안하면, 다음 코드들은 실행이 안되는 형태 인듯
        assumeTrue("LOCAL".equals(System.getenv("TEST_ENV")));
        assertTrue(true);

        System.out.println("create1");
    }

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

    @BeforeAll
    static void beforAll() {

        System.out.println("before all");
    }


}