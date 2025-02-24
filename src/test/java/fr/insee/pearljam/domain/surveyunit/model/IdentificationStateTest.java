package fr.insee.pearljam.domain.surveyunit.model;

import static org.assertj.core.api.Assertions.assertThat;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.question.AccessQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.CategoryQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.IdentificationQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.IndividualStatusQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.OccupantQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.SituationQuestionValue;
import java.util.ArrayList;
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
        assertThat(IdentificationState.getState(identificationToCheck,
            IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.MISSING));
  }

  @Test
  @DisplayName("Should return FINISHED when identification question value is destroy or unidentified")
  void testState02() {
    Identification identificationDestroy = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.DESTROY, null, null, null, null, null, null, null, null, null);
    Identification identificationUnidentified = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.UNIDENTIFIED, null, null, null, null, null, null, null, null,
        null);

    List<Identification> identifications = List.of(identificationDestroy,
        identificationUnidentified);
    identifications.forEach(identificationToCheck ->
        assertThat(IdentificationState.getState(identificationToCheck,
            IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
  }

  @Test
  @DisplayName("Should return ONGOING state when access is null")
  void testState03() {
    Identification identificationToCheck = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, null, null, null, null, null, null, null, null,
        null);

    assertThat(IdentificationState.getState(identificationToCheck,
        IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
  }

  @Test
  @DisplayName("Should return ONGOING when situation question value is null")
  void testState04() {
    Identification identificationToCheck = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, null, null, null, null,
        null, null,
        null, null);

    assertThat(IdentificationState.getState(identificationToCheck,
        IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
  }

  @Test
  @DisplayName("Should return FINISHED when situation question value is absorbed or no ordinary")
  void testState05() {
    Identification identificationAbsorbed = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
        SituationQuestionValue.ABSORBED,
        null, null, null, null, null, null, null);
    Identification identificationNoOrdinary = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC,
        SituationQuestionValue.NOORDINARY,
        null, null, null, null, null, null, null);

    List<Identification> identifications = List.of(identificationAbsorbed,
        identificationNoOrdinary);
    identifications.forEach(identificationToCheck ->
        assertThat(IdentificationState.getState(identificationToCheck,
            IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
  }

  @Test
  @DisplayName("Should return ONGOING when category question value is null")
  void testState06() {
    Identification identificationToCheck = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
        SituationQuestionValue.ORDINARY,
        null, null, null, null, null, null, null);

    assertThat(IdentificationState.getState(identificationToCheck,
        IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
  }

  @Test
  @DisplayName("Should return FINISHED when category question value is vacant or secondary")
  void testState07() {
    Identification identificationVacant = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
        SituationQuestionValue.ORDINARY,
        CategoryQuestionValue.VACANT, null, null, null, null, null, null);
    Identification identificationSecondary = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC,
        SituationQuestionValue.ORDINARY,
        CategoryQuestionValue.SECONDARY, null, null, null, null, null, null);

    List<Identification> identifications = List.of(identificationVacant, identificationSecondary);
    identifications.forEach(identificationToCheck ->
        assertThat(IdentificationState.getState(identificationToCheck,
            IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED));
  }

  @Test
  @DisplayName("Should return FINISHED when occupant is not null")
  void testState08() {
    Identification identificationToCheck = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
        SituationQuestionValue.ORDINARY,
        CategoryQuestionValue.OCCASIONAL, OccupantQuestionValue.IDENTIFIED, null, null, null, null,
        null);

    assertThat(IdentificationState.getState(identificationToCheck,
        IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.FINISHED);
  }

  @Test
  @DisplayName("Should return ONGOING state")
  void testState09() {
    Identification identificationToCheck = new Identification(1L,
        IdentificationType.HOUSEF2F,
        IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC,
        SituationQuestionValue.ORDINARY,
        CategoryQuestionValue.OCCASIONAL, null, null, null, null, null, null);

    assertThat(IdentificationState.getState(identificationToCheck,
        IdentificationConfiguration.HOUSEF2F)).isEqualTo(IdentificationState.ONGOING);
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
        // Cases 1-2 : Null Identification
        new TestCase(null, IdentificationConfiguration.NOIDENT, IdentificationState.MISSING),
        new TestCase(null, IdentificationConfiguration.HOUSEF2F, IdentificationState.MISSING),

        // Case 3: NOIDENT always returns MISSING
        new TestCase(Identification.builder().build(), IdentificationConfiguration.NOIDENT,
            IdentificationState.MISSING),

        // Cases 4-5: HOUSEF2F + `identification`[DESTROY / UNIDENTIFIED] => state FINISHED
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.DESTROY,
                AccessQuestionValue.ACC,
                // N/A pour DESTROY et UNIDENTIFIED, mais peut être ACC ou NACC
                SituationQuestionValue.ORDINARY, // N/A pour DESTROY et UNIDENTIFIED
                CategoryQuestionValue.PRIMARY, // N/A pour DESTROY et UNIDENTIFIED
                OccupantQuestionValue.UNIDENTIFIED // N/A pour DESTROY et UNIDENTIFIED
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
        ),
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.UNIDENTIFIED,
                AccessQuestionValue.ACC,
                // N/A pour DESTROY et UNIDENTIFIED, mais peut être ACC ou NACC
                SituationQuestionValue.ORDINARY, // N/A pour DESTROY et UNIDENTIFIED
                CategoryQuestionValue.PRIMARY, // N/A pour DESTROY et UNIDENTIFIED
                OccupantQuestionValue.UNIDENTIFIED // N/A pour DESTROY et UNIDENTIFIED
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
        ),
        // Case 6: HOUSEF2F + `identification`[IDENTIFIED] + `access`[null] => ONGOING
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                null, // Aucun accès ici, car null
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.UNIDENTIFIED // Occupant non identifié
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
        ),
        // Case 7-8: HOUSEF2F + `identification`[IDENTIFIED] + `access`[ACC / NACC] => ONGOING
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC, // ACC
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                null
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
        ),
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.NACC, // NACC
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                null
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
        ),
        // Case 9-10: HOUSEF2F previous + `situation`[ABSORBED/NOORDINARY] => FINISHED
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ABSORBED, // Absorbed situation
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
        ),
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.NOORDINARY, // Noordinary situation
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
        ),
        // Case 11-12: HOUSEF2F previous + `category`[SECONDARY / VACANT] => FINISHED
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.SECONDARY, // Secondary category
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
        ),
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.VACANT, // Vacant category
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
        ),
        // Case 13: HOUSEF2F previous + `category`[PRIMARY] with null occupant => ONGOING
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY, // Primary category
                null // Occupant null
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING
        ),
        // HOUSEF2F 14-15 questions completed
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.IDENTIFIED // Identified occupant
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
        ),
        new TestCase(
            buildHouseF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.UNIDENTIFIED // Unidentified occupant
            ),
            IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED
        ),
        new TestCase(
            Identification.builder().build(), IdentificationConfiguration.HOUSETEL,
            IdentificationState.MISSING
        ),
        new TestCase(
            buildIndividuTelIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.DCD,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.IDENTIFIED
            ),
            IdentificationConfiguration.INDTEL, IdentificationState.FINISHED
        ),
        new TestCase(
            buildIndividuTelIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.DCD,
                AccessQuestionValue.NACC,
                SituationQuestionValue.ABSORBED,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDTEL, IdentificationState.FINISHED
        ),
        new TestCase(
            buildIndividuTelIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.NOIDENT,
                AccessQuestionValue.ACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.IDENTIFIED
            ),
            IdentificationConfiguration.INDTEL, IdentificationState.FINISHED
        ),
        new TestCase(
            buildIndividuTelIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.NOFIELD,
                AccessQuestionValue.NACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDTEL, IdentificationState.FINISHED
        ),
        new TestCase(
            buildIndividuTelIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.SAME_ADDRESS,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.IDENTIFIED
            ),
            IdentificationConfiguration.INDTEL, IdentificationState.FINISHED
        ),
        new TestCase(
            buildIndividuTelIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.SAME_ADDRESS,
                AccessQuestionValue.ACC,
                null,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDTEL, IdentificationState.ONGOING
        ),
        new TestCase(
            buildIndividuTelIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                null,
                AccessQuestionValue.NACC,
                SituationQuestionValue.ABSORBED,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.IDENTIFIED
            ),
            IdentificationConfiguration.INDTEL, IdentificationState.ONGOING
        ),
        // Test for INDF2F configuration - IndividualStatusQuestionValue is DCD
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.DCD,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY,
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.IDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.FINISHED
        ),

        // Test for INDF2F configuration - IndividualStatusQuestionValue is NOIDENT
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.UNIDENTIFIED,
                IndividualStatusQuestionValue.NOIDENT,
                AccessQuestionValue.NACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.FINISHED
        ),

        // Test for INDF2F configuration - IndividualStatusQuestionValue is NOFIELD
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.DESTROY,
                IndividualStatusQuestionValue.NOFIELD,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ABSORBED,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.FINISHED
        ),
        // Test for INDF2F configuration - IndividualStatusQuestionValue is SAME_ADDRESS with situation null
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.SAME_ADDRESS,
                AccessQuestionValue.ACC,
                null, // Situation is null
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.ONGOING
        ),
        // Test for INDF2F configuration - IndividualStatusQuestionValue is SAME_ADDRESS with situation not null
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.SAME_ADDRESS,
                AccessQuestionValue.ACC,
                SituationQuestionValue.ORDINARY, // Situation is not null
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.IDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.FINISHED
        ),
        // Test for INDF2F configuration - IndividualStatusQuestionValue is OTHER_ADDRESS with interviewer null
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.OTHER_ADDRESS,
                AccessQuestionValue.ACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.ONGOING
        ),
        // Test for INDF2F configuration - IndividualStatusQuestionValue is OTHER_ADDRESS with interviewer NO
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.OTHER_ADDRESS,
                AccessQuestionValue.NACC,
                SituationQuestionValue.ABSORBED,
                CategoryQuestionValue.VACANT,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.ONGOING
        ),

        // Test for INDF2F configuration - IndividualStatusQuestionValue is OTHER_ADDRESS with interviewer YES and situation null
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.OTHER_ADDRESS,
                AccessQuestionValue.ACC,
                null, // Situation is null
                CategoryQuestionValue.PRIMARY,
                OccupantQuestionValue.IDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.ONGOING
        ),

        // Test for INDF2F configuration - IndividualStatusQuestionValue is SAME_ADDRESS with situation NOORDINARY
        new TestCase(
            buildIndividuF2FIdentification(
                IdentificationQuestionValue.IDENTIFIED,
                IndividualStatusQuestionValue.SAME_ADDRESS,
                AccessQuestionValue.ACC,
                SituationQuestionValue.NOORDINARY,
                CategoryQuestionValue.SECONDARY,
                OccupantQuestionValue.UNIDENTIFIED
            ),
            IdentificationConfiguration.INDF2F, IdentificationState.FINISHED
        )
    );
  }


  private static Identification buildHouseF2FIdentification(
      IdentificationQuestionValue identification,
      AccessQuestionValue access,
      SituationQuestionValue situation,
      CategoryQuestionValue category,
      OccupantQuestionValue occupant) {

    return Identification.builder()
        .identification(identification)
        .access(access)
        .situation(situation)
        .category(category)
        .occupant(occupant)
        .build();
  }


  private static Identification buildIndividuTelIdentification(
      IdentificationQuestionValue identification,
      IndividualStatusQuestionValue individualStatus,
      AccessQuestionValue access,
      SituationQuestionValue situation,
      CategoryQuestionValue category,
      OccupantQuestionValue occupant) {

    return Identification.builder()
        .individualStatus(individualStatus)
        .access(access)
        .situation(situation)
        .category(category)
        .occupant(occupant)
        .identification(identification)
        .build();
  }

  private static Identification buildIndividuF2FIdentification(
      IdentificationQuestionValue identification,
      IndividualStatusQuestionValue individualStatus,
      AccessQuestionValue access,
      SituationQuestionValue situation,
      CategoryQuestionValue category,
      OccupantQuestionValue occupant) {

    return Identification.builder()
        .individualStatus(individualStatus)
        .access(access)
        .situation(situation)
        .category(category)
        .occupant(occupant)
        .identification(identification)
        .build();
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
