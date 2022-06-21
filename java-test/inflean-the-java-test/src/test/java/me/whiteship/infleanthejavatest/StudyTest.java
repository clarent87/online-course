package me.whiteship.infleanthejavatest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
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
    @ValueSource(strings = {"날씨가", "많이", "추워지고", "있습니다."}) // strings 말고 다양한 attribute있음
    @NullSource
    @EmptySource
    void parameterizedTest(String message) {
        System.out.println("message = " + message);
    }

    @DisplayName("스터디 만들기")
    @ParameterizedTest( name = "{index} {displayName} message = {0}") // test method의 param을 {0}으로 받음.
    @ValueSource(ints = {10,20,30})
    void parameterizedTest2(Integer message) {
        System.out.println("message = " + message);
    }


    @DisplayName("스터디 만들기")
    @ParameterizedTest( name = "{index} {displayName} message = {0}") // test method의 param을 {0}으로 받음.
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


    @BeforeAll
    static void beforAll() {

        System.out.println("before all");
    }


}