package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IdentificationStateTest {

    @Test
    @DisplayName("Should return MISSING when identification is null")
    void testState01() {
        Identification identification1 = new Identification(1L,
                IdentificationType.HOUSEF2F,
                null,
                AccessQuestionValue.NACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED,
                null, null, null, null, null
        );

        List<Identification> identifications = new ArrayList<>();
        identifications.add(identification1);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.MISSING));
    }

    @Test
    @DisplayName("Should return FINISHED when identification question value is destroy or unidentified")
    void testState02() {
        Identification identificationDestroy = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.DESTROY, null, null, null, null, null, null, null, null, null);
        Identification identificationUnidentified = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.UNIDENTIFIED, null, null, null, null, null, null, null, null, null);

        List<Identification> identifications = List.of(identificationDestroy, identificationUnidentified);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return ONGOING state when access is null")
    void testState03() {
        Identification identificationToCheck = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, null, null, null, null, null, null, null, null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Should return ONGOING when situation question value is null")
    void testState04() {
        Identification identificationToCheck = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, null, null, null, null, null, null,
                null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Should return FINISHED when situation question value is absorbed or no ordinary")
    void testState05() {
        Identification identificationAbsorbed = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ABSORBED,
                null, null, null, null, null, null, null);
        Identification identificationNoOrdinary = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, SituationQuestionValue.NOORDINARY,
                null, null, null, null, null, null, null);

        List<Identification> identifications = List.of(identificationAbsorbed, identificationNoOrdinary);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return ONGOING when category question value is null")
    void testState06() {
        Identification identificationToCheck = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
                null, null, null, null, null, null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Should return FINISHED when category question value is vacant or secondary")
    void testState07() {
        Identification identificationVacant = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.VACANT, null, null, null, null, null, null);
        Identification identificationSecondary = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.SECONDARY, null, null, null, null, null, null);

        List<Identification> identifications = List.of(identificationVacant, identificationSecondary);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return FINISHED when occupant is not null")
    void testState08() {
        Identification identificationToCheck = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.OCCASIONAL, OccupantQuestionValue.IDENTIFIED, null, null, null, null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED);
    }

    @Test
    @DisplayName("Should return ONGOING state")
    void testState09() {
        Identification identificationToCheck = new Identification(1L,
                IdentificationType.HOUSEF2F,
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.OCCASIONAL, null, null, null, null, null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
    }

    @TestFactory
    @DisplayName("Parameterized tests for getState method")
    Stream<DynamicTest> testGetState() {
        return provideTestCases().map(testCase -> DynamicTest.dynamicTest(
                testCase.getTestName(),
                () -> {
                    IdentificationState result = IdentificationState.getState(
                            testCase.identification, testCase.configuration
                    );
                    assertThat(testCase.expectedState).isEqualTo(result);
                }
        ));
    }


    @Test
    @DisplayName("Given IDENTIFIED status, when identification is set, then state should be ONGOING")
    void testStateIDENTIFIED() {
        // Given
        Identification identificationToCheck = new Identification(1L,
            IdentificationType.INDTEL,
            IdentificationQuestionValue.IDENTIFIED, // IDENTIFIED status
            AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
            CategoryQuestionValue.PRIMARY, null, null, null, null, null, null);

        // When
        IdentificationState result = IdentificationState.getState(identificationToCheck, IdentificationConfiguration.INDTEL);

        // Then
        assertThat(result).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Given DESTROY status, when identification is set, then state should be ONGOING")
    void testStateDESTROY() {
        // Given
        Identification identificationToCheck = new Identification(1L,
            IdentificationType.INDTEL,
            IdentificationQuestionValue.DESTROY, // DESTROY status
            AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
            CategoryQuestionValue.PRIMARY, null, null, null, null, null, null);

        // When
        IdentificationState result = IdentificationState.getState(identificationToCheck, IdentificationConfiguration.INDTEL);

        // Then
        assertThat(result).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Given UNIDENTIFIED status, when identification is set, then state should be ONGOING")
    void testStateUNIDENTIFIED() {
        // Given
        Identification identificationToCheck = new Identification(1L,
            IdentificationType.INDTEL,
            IdentificationQuestionValue.UNIDENTIFIED, // UNIDENTIFIED status
            AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
            CategoryQuestionValue.PRIMARY, null, null, null, null, null, null);

        // When
        IdentificationState result = IdentificationState.getState(identificationToCheck, IdentificationConfiguration.INDTEL);

        // Then
        assertThat(result).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Given NULL identification status, when processed, then state should be MISSING")
    void testStateWithNullIdentification() {
        // Given
        Identification identificationToCheck = new Identification(1L,
            IdentificationType.INDTEL,
            null, // IdentificationQuestionValue is null
            AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
            CategoryQuestionValue.PRIMARY, null, null, null, null, null, null);

        // When
        IdentificationState result = IdentificationState.getState(identificationToCheck, IdentificationConfiguration.INDTEL);

        // Then
        assertThat(result).isEqualTo(IdentificationState.MISSING);
    }

    @Test
    @DisplayName("Given VACANT category, when processed, then state should be ONGOING")
    void testStateWithVacantCategory() {
        // Given
        Identification identificationToCheck = new Identification(1L,
            IdentificationType.INDTEL,
            IdentificationQuestionValue.UNIDENTIFIED, // UNIDENTIFIED status
            AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
            CategoryQuestionValue.VACANT, null, null, null, null, null, null);

        // When
        IdentificationState result = IdentificationState.getState(identificationToCheck, IdentificationConfiguration.INDTEL);

        // Then
        assertThat(result).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Given IDENTIFIED status, when configuration is NOIDENT, then state should be MISSING")
    void testStateWithNoIdentConfiguration() {
        // Given
        Identification identificationToCheck = new Identification(1L,
            IdentificationType.INDTEL,
            IdentificationQuestionValue.IDENTIFIED, // IDENTIFIED status
            AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
            CategoryQuestionValue.PRIMARY, null, null, null, null, null, null);

        // When
        IdentificationState result = IdentificationState.getState(identificationToCheck, IdentificationConfiguration.NOIDENT);

        // Then
        assertThat(result).isEqualTo(IdentificationState.MISSING);
    }

    /// IASCO & HOUSEF2F cases table : --- mean identificationStatus = FINISHED and question should be null and won't
    ///  be evaluated
    ///
    /// | identification | access     | situation             | category           | occupant                  |
    /// | -------------- | ---------- | --------------------- | ------------------ | ------------------------- |
    /// | DESTROY        | ---        | ---                   | ---                | ---                       |
    /// | UNIDENTIFIED   | ---        | ---                   | ---                | ---                       |
    /// | IDENTIFIED     | ACC / NACC | ABSORBED / NOORDINARY | ---                | ---                       |
    /// | IDENTIFIED     | ACC / NACC | ORDINARY              | SECONDARY / VACANT | ---                       |
    /// | IDENTIFIED     | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED / UNIDENTIFIED |
    private static Stream<TestCase> provideTestCases() {
        return Stream.of(
                // Cases 1-2 : Null Identification
                new TestCase(null, IdentificationConfiguration.NOIDENT, IdentificationState.MISSING),
                new TestCase(null, IdentificationConfiguration.HOUSEF2F, IdentificationState.MISSING),

                // Case 3: NOIDENT always returns MISSING
                new TestCase(Identification.builder().build(), IdentificationConfiguration.NOIDENT,
                        IdentificationState.MISSING),

                // Cases 4-5: HOUSEF2F + `identification`[DESTROY / UNIDENTIFIED] => state FINISHED
                new TestCase(
                        houseF2FIdentBuilder(IdentificationQuestionValue.DESTROY).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),
                new TestCase(
                        houseF2FIdentBuilder(IdentificationQuestionValue.UNIDENTIFIED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),

                // Case 6: HOUSEF2F + `identification`[IDENTIFIED] + `access`[null] => ONGOING
                new TestCase(
                        houseF2FIdentBuilder(IdentificationQuestionValue.IDENTIFIED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
                ),

                // Case 7-8: HOUSEF2F + `identification`[IDENTIFIED] + `access`[ACC / NACC] => ONGOING
                new TestCase(
                        houseF2FAccessBuilder(AccessQuestionValue.ACC).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
                ),
                new TestCase(
                        houseF2FAccessBuilder(AccessQuestionValue.NACC).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
                ),


                // Case 9-10: HOUSEF2F previous +  `situation`[ABSORBED/NOORDINARY] => FINISHED
                new TestCase(
                        houseF2FSituationBuilder(SituationQuestionValue.ABSORBED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),
                new TestCase(
                        houseF2FSituationBuilder(SituationQuestionValue.NOORDINARY).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),

                // Case 11-12: HOUSEF2F previous + `category`[SECONDARY / VACANT] => FINISHED
                new TestCase(
                        houseF2FCategoryBuilder(CategoryQuestionValue.SECONDARY).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),
                new TestCase(
                        houseF2FCategoryBuilder(CategoryQuestionValue.VACANT).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),

                // Case 13: HOUSEF2F previous + `category`[PRIMARY] with null occupant => ONGOING
                new TestCase(
                        houseF2FCategoryBuilder(CategoryQuestionValue.PRIMARY).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
                ),

                // HOUSEF2F 14-15 questions completed
                new TestCase(
                        houseF2FOccupantBuilder(OccupantQuestionValue.IDENTIFIED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),
                new TestCase(
                        houseF2FOccupantBuilder(OccupantQuestionValue.UNIDENTIFIED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),

                // Case 16: Configuration HOUSETEL (and other unimplemented identification configurations) always
                // returns MISSING
                new TestCase(
                        Identification.builder().build(), IdentificationConfiguration.HOUSETEL,
                        IdentificationState.MISSING
                ));
    }


    // Builders

    private static Identification.IdentificationBuilder houseF2FIdentBuilder(IdentificationQuestionValue identification) {
        return Identification.builder().identification(identification);
    }

    private static Identification.IdentificationBuilder houseF2FAccessBuilder(AccessQuestionValue access) {
        return houseF2FIdentBuilder(IdentificationQuestionValue.IDENTIFIED).access(access);
    }

    private static Identification.IdentificationBuilder houseF2FSituationBuilder(SituationQuestionValue situation) {
        return houseF2FAccessBuilder(AccessQuestionValue.ACC).situation(situation);
    }

    private static Identification.IdentificationBuilder houseF2FCategoryBuilder(CategoryQuestionValue category) {
        return houseF2FSituationBuilder(SituationQuestionValue.ORDINARY).category(category);
    }

    private static Identification.IdentificationBuilder houseF2FOccupantBuilder(OccupantQuestionValue occupant) {
        return houseF2FCategoryBuilder(CategoryQuestionValue.PRIMARY).occupant(occupant);
    }


    // Helper class to represent a test case
    private record TestCase(Identification identification, IdentificationConfiguration configuration,
                            IdentificationState expectedState) {
        String getTestName() {
            return String.format(
                    "Given configuration %s, identification %s, expect state %s",
                    configuration, identification, expectedState
            );
        }

    }

}
