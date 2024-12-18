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
        Identification identification1 = new Identification(
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
        Identification identificationDestroy = new Identification(
                IdentificationQuestionValue.DESTROY, null, null, null, null, null, null, null, null, null);
        Identification identificationUnidentified = new Identification(
                IdentificationQuestionValue.UNIDENTIFIED, null, null, null, null, null, null, null, null, null);

        List<Identification> identifications = List.of(identificationDestroy, identificationUnidentified);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return ONGOING state when access is null")
    void testState03() {
        Identification identificationToCheck = new Identification(
                IdentificationQuestionValue.IDENTIFIED, null, null, null, null, null, null, null, null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Should return ONGOING when situation question value is null")
    void testState04() {
        Identification identificationToCheck = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, null, null, null, null, null, null,
                null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Should return FINISHED when situation question value is absorbed or no ordinary")
    void testState05() {
        Identification identificationAbsorbed = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ABSORBED,
                null, null, null, null, null, null, null);
        Identification identificationNoOrdinary = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, SituationQuestionValue.NOORDINARY,
                null, null, null, null, null, null, null);

        List<Identification> identifications = List.of(identificationAbsorbed, identificationNoOrdinary);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return ONGOING when category question value is null")
    void testState06() {
        Identification identificationToCheck = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
                null, null, null, null, null, null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
    }

    @Test
    @DisplayName("Should return FINISHED when category question value is vacant or secondary")
    void testState07() {
        Identification identificationVacant = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.VACANT, null, null, null, null, null, null);
        Identification identificationSecondary = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.SECONDARY, null, null, null, null, null, null);

        List<Identification> identifications = List.of(identificationVacant, identificationSecondary);
        identifications.forEach(identificationToCheck ->
                assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
    }

    @Test
    @DisplayName("Should return FINISHED when occupant is not null")
    void testState08() {
        Identification identificationToCheck = new Identification(
                IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.OCCASIONAL, OccupantQuestionValue.IDENTIFIED, null, null, null, null, null);

        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED);
    }

    @Test
    @DisplayName("Should return ONGOING state")
    void testState09() {
        Identification identificationToCheck = new Identification(
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
                        iascoIdentBuilder(IdentificationQuestionValue.DESTROY).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),
                new TestCase(
                        iascoIdentBuilder(IdentificationQuestionValue.UNIDENTIFIED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),

                // Case 6: HOUSEF2F + `identification`[IDENTIFIED] + `access`[null] => ONGOING
                new TestCase(
                        iascoIdentBuilder(IdentificationQuestionValue.IDENTIFIED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
                ),

                // Case 7-8: HOUSEF2F + `identification`[IDENTIFIED] + `access`[ACC / NACC] => ONGOING
                new TestCase(
                        iascoAccessBuilder(AccessQuestionValue.ACC).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
                ),
                new TestCase(
                        iascoAccessBuilder(AccessQuestionValue.NACC).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
                ),


                // Case 9-10: HOUSEF2F previous +  `situation`[ABSORBED/NOORDINARY] => FINISHED
                new TestCase(
                        iascoSituationBuilder(SituationQuestionValue.ABSORBED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),
                new TestCase(
                        iascoSituationBuilder(SituationQuestionValue.NOORDINARY).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),

                // Case 11-12: HOUSEF2F previous + `category`[SECONDARY / VACANT] => FINISHED
                new TestCase(
                        iascoCategoryBuilder(CategoryQuestionValue.SECONDARY).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),
                new TestCase(
                        iascoCategoryBuilder(CategoryQuestionValue.VACANT).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),

                // Case 13: HOUSEF2F previous + `category`[PRIMARY] with null occupant => ONGOING
                new TestCase(
                        iascoCategoryBuilder(CategoryQuestionValue.PRIMARY).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
                ),

                // HOUSEF2F 14-15 questions completed
                new TestCase(
                        iascoOccupantBuilder(OccupantQuestionValue.IDENTIFIED).build(),
                        IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
                ),
                new TestCase(
                        iascoOccupantBuilder(OccupantQuestionValue.UNIDENTIFIED).build(),
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

    private static Identification.IdentificationBuilder iascoIdentBuilder(IdentificationQuestionValue identification) {
        return Identification.builder().identification(identification);
    }

    private static Identification.IdentificationBuilder iascoAccessBuilder(AccessQuestionValue access) {
        return iascoIdentBuilder(IdentificationQuestionValue.IDENTIFIED).access(access);
    }

    private static Identification.IdentificationBuilder iascoSituationBuilder(SituationQuestionValue situation) {
        return iascoAccessBuilder(AccessQuestionValue.ACC).situation(situation);
    }

    private static Identification.IdentificationBuilder iascoCategoryBuilder(CategoryQuestionValue category) {
        return iascoSituationBuilder(SituationQuestionValue.ORDINARY).category(category);
    }

    private static Identification.IdentificationBuilder iascoOccupantBuilder(OccupantQuestionValue occupant) {
        return iascoCategoryBuilder(CategoryQuestionValue.PRIMARY).occupant(occupant);
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
