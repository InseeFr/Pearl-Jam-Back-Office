package fr.insee.pearljam.domain.surveyunit.model;

import static org.assertj.core.api.Assertions.assertThat;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.question.*;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

class IdentificationStateTest {

  @Test
  @DisplayName("Should return MISSING when identification is null")
  void testState01() {
    Identification identification = new Identification(1L, IdentificationType.HOUSEF2F, null,
        AccessQuestionValue.NACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.VACANT,
        OccupantQuestionValue.UNIDENTIFIED, null, null, null, null, null);

    assertThat(IdentificationState.getState(identification, IdentificationConfiguration.HOUSEF2F))
        .isEqualTo(IdentificationState.MISSING);
  }

  @Test
  @DisplayName("Should return FINISHED when identification question value is DESTROY or UNIDENTIFIED")
  void testState02() {
    Identification identificationDestroy = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.DESTROY, null, null, null, null, null, null, null, null, null);
    Identification identificationUnidentified = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.UNIDENTIFIED, null, null, null, null, null, null, null, null, null);

    List<Identification> identifications = List.of(identificationDestroy, identificationUnidentified);
    identifications.forEach(identificationToCheck ->
        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F))
            .isEqualTo(IdentificationState.FINISHED));
  }

  @Test
  @DisplayName("Should return ONGOING state when access or situation question value is null")
  void testState03() {
    Identification identificationAccessNull = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, null, null, null, null, null, null, null, null, null);
    Identification identificationSituationNull = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, null, null, null, null, null, null, null, null);

    List<Identification> identifications = List.of(identificationAccessNull, identificationSituationNull);
    identifications.forEach(identificationToCheck ->
        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F))
            .isEqualTo(IdentificationState.ONGOING));
  }

  @Test
  @DisplayName("Should return FINISHED when situation question value is ABSORBED or NOORDINARY")
  void testState04() {
    Identification identificationAbsorbed = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ABSORBED,
        null, null, null, null, null, null, null);
    Identification identificationNoOrdinary = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, SituationQuestionValue.NOORDINARY,
        null, null, null, null, null, null, null);

    List<Identification> identifications = List.of(identificationAbsorbed, identificationNoOrdinary);
    identifications.forEach(identificationToCheck ->
        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F))
            .isEqualTo(IdentificationState.FINISHED));
  }

  @Test
  @DisplayName("Should return ONGOING when category question value is null")
  void testState05() {
    Identification identification = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
        null, null, null, null, null, null, null);

    assertThat(IdentificationState.getState(identification, IdentificationConfiguration.HOUSEF2F))
        .isEqualTo(IdentificationState.ONGOING);
  }

  @Test
  @DisplayName("Should return FINISHED when category question value is VACANT or SECONDARY")
  void testState06() {
    Identification identificationVacant = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
        CategoryQuestionValue.VACANT, null, null, null, null, null, null);
    Identification identificationSecondary = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, SituationQuestionValue.ORDINARY,
        CategoryQuestionValue.SECONDARY, null, null, null, null, null, null);

    List<Identification> identifications = List.of(identificationVacant, identificationSecondary);
    identifications.forEach(identificationToCheck ->
        assertThat(IdentificationState.getState(identificationToCheck, IdentificationConfiguration.HOUSEF2F))
            .isEqualTo(IdentificationState.FINISHED));
  }

  @Test
  @DisplayName("Should return FINISHED when occupant is not null")
  void testState07() {
    Identification identification = new Identification(1L, IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY,
        CategoryQuestionValue.OCCASIONAL, OccupantQuestionValue.IDENTIFIED, null, null, null, null, null);

    assertThat(IdentificationState.getState(identification, IdentificationConfiguration.HOUSEF2F))
        .isEqualTo(IdentificationState.FINISHED);
  }

  @TestFactory
  @DisplayName("Parameterized tests for getState method")
  Stream<DynamicTest> testGetState() {
    return provideTestCases().map(testCase -> DynamicTest.dynamicTest(
        testCase.getTestName(),
        () -> {
          IdentificationState result = IdentificationState.getState(testCase.identification, testCase.configuration);
          assertThat(result).isEqualTo(testCase.expectedState);
        }
    ));
  }

  /// IASCO & HOUSEF2F cases table : --- mean identificationStatus = FINISHED and question should be
  /// null and won't be evaluated
  ///
  /// | identification  | access     | situation             | category           | occupant              | IdentificationConfiguration |
  /// | --------------- | ---------- | --------------------- | ------------------ | --------------------- | ---------------------------- |
  /// | DESTROY         | ---        | ---                   | ---                | ---                   | HOUSEF2F                    |
  /// | UNIDENTIFIED    | ---        | ---                   | ---                | ---                   | HOUSEF2F                    |
  /// | IDENTIFIED      | ACC / NACC | ABSORBED / NOORDINARY | ---                | ---                   | HOUSEF2F                    |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | SECONDARY / VACANT | ---                   | HOUSEF2F                    |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED / UNIDENTIFIED | HOUSEF2F                  |
  /// | IDENTIFIED      | null       | ORDINARY              | PRIMARY            | UNIDENTIFIED / null    | HOUSEF2F                    |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | null                   | HOUSEF2F                    |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | SECONDARY / VACANT | UNIDENTIFIED           | HOUSEF2F                    |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED             | HOUSEF2F                    |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED             | INDTEL                      |
  /// | IDENTIFIED      | NACC       | ABSORBED               | VACANT             | UNIDENTIFIED           | INDTEL                      |
  private static Stream<TestCase> provideTestCases() {
    return Stream.of(
        new TestCase(null, IdentificationConfiguration.NOIDENT, IdentificationState.MISSING),
        new TestCase(null, IdentificationConfiguration.HOUSEF2F, IdentificationState.MISSING),
        new TestCase(Identification.builder().build(), IdentificationConfiguration.NOIDENT, IdentificationState.MISSING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.DESTROY, AccessQuestionValue.ACC,
            SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.UNIDENTIFIED, AccessQuestionValue.ACC,
            SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, null,
            SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
            null, CategoryQuestionValue.PRIMARY, null),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
            SituationQuestionValue.ABSORBED, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
            SituationQuestionValue.NOORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
            SituationQuestionValue.ORDINARY, CategoryQuestionValue.SECONDARY, OccupantQuestionValue.UNIDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
            SituationQuestionValue.ORDINARY, CategoryQuestionValue.VACANT, OccupantQuestionValue.UNIDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
            SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, null),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
            SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.IDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
            SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED)
    );
  }

  private static Identification buildHouseF2FIdentification(IdentificationQuestionValue identification,
      AccessQuestionValue access, SituationQuestionValue situation, CategoryQuestionValue category,
      OccupantQuestionValue occupant) {
    return Identification.builder()
        .identification(identification)
        .access(access)
        .situation(situation)
        .category(category)
        .occupant(occupant)
        .build();
  }

  private record TestCase(Identification identification, IdentificationConfiguration configuration,
                          IdentificationState expectedState) {
    String getTestName() {
      return String.format("Given configuration %s, identification %s, expect state %s",
          configuration, identification, expectedState);
    }
  }
}
