package fr.insee.pearljam.domain.surveyunit.model;

import static org.assertj.core.api.Assertions.assertThat;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.question.AccessQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.CategoryQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.IdentificationQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.IndividualStatusQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.InterviewerCanProcessQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.OccupantQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.SituationQuestionValue;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

class IdentificationStateTest {

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

  /// | identification  | access     | situation             | category           | occupant              | interviewer         | IdentificationConfiguration | État attendu |
  /// | --------------- | ---------- | --------------------- | ------------------ | --------------------- | ------------------- | ---------------------------- | ------------ |
  /// | DESTROY         | ---        | ---                   | ---                | ---                   | ---                 | HOUSEF2F                    | FINISHED     |
  /// | UNIDENTIFIED    | ---        | ---                   | ---                | ---                   | ---                 | HOUSEF2F                    | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ABSORBED / NOORDINARY| ---                | ---                   | ---                 | HOUSEF2F                    | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | SECONDARY / VACANT | ---                   | ---                 | HOUSEF2F                    | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED / UNIDENTIFIED | ---                 | HOUSEF2F                    | FINISHED     |
  /// | IDENTIFIED      | null       | ORDINARY              | PRIMARY            | UNIDENTIFIED / null   | ---                 | HOUSEF2F                    | ONGOING      |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | null                  | ---                 | HOUSEF2F                    | ONGOING      |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | SECONDARY / VACANT | UNIDENTIFIED          | ---                 | HOUSEF2F                    | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED            | ---                 | HOUSEF2F                    | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | null                  | null               | null                  | ---                 | HOUSEF2F                    | ONGOING     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED            | ---                 | INDTEL                      | FINISHED     |
  /// | IDENTIFIED      | NACC       | ABSORBED              | VACANT             | UNIDENTIFIED          | ---                 | INDTEL                      | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED            | ---                 | INDF2F                      | FINISHED     |
  /// | IDENTIFIED      | NACC       | ABSORBED              | VACANT             | UNIDENTIFIED          | ---                 | INDF2F                      | FINISHED     |
  /// | IDENTIFIED      | ---        | ---                   | ---                | ---                   | ---                 | INDF2F                      | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ABSORBED / NOORDINARY| ---                | ---                   | ---                 | INDF2F                      | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | SECONDARY / VACANT | ---                   | ---                 | INDF2F                      | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED / UNIDENTIFIED | ---                 | INDF2F                      | FINISHED     |
  /// | IDENTIFIED      | null       | ORDINARY              | PRIMARY            | UNIDENTIFIED / null   | ---                 | INDF2F                      | ONGOING      |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | null                  | ---                 | INDF2F                      | ONGOING      |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | SECONDARY / VACANT | UNIDENTIFIED          | ---                 | INDF2F                      | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | IDENTIFIED            | ---                 | INDF2F                      | FINISHED     |
  /// | IDENTIFIED      | ACC / NACC | ORDINARY              | PRIMARY            | UNIDENTIFIED          | YES                 | INDF2F                      | ONGOING      |
  /// | IDENTIFIED      | ACC / NACC | NOORDINARY            | PRIMARY            | UNIDENTIFIED          | NO                  | INDF2F                      | ONGOING      |
  /// | IDENTIFIED      | ACC / NACC | ABSORBED              | PRIMARY            | UNIDENTIFIED          | ---                 | INDF2F                      | FINISHED     |

  private static Stream<TestCase> provideTestCases() {
    return Stream.of(
        new TestCase(null, IdentificationConfiguration.NOIDENT, IdentificationState.MISSING),
        new TestCase(null, IdentificationConfiguration.HOUSEF2F, IdentificationState.MISSING),
        new TestCase(Identification.builder().build(), IdentificationConfiguration.NOIDENT, IdentificationState.MISSING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.DESTROY, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.UNIDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, null, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, null), IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.NACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, null), IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ABSORBED, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.NOORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.SECONDARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.VACANT, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, null), IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.IDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.HOUSEF2F, IdentificationState.FINISHED),
        new TestCase(buildHouseF2FIdentification(IdentificationQuestionValue.IDENTIFIED, AccessQuestionValue.ACC, null,null,null), IdentificationConfiguration.HOUSEF2F, IdentificationState.ONGOING),

        new TestCase(Identification.builder().build(), IdentificationConfiguration.HOUSETEL, IdentificationState.MISSING),
        new TestCase(buildIndividuTelIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.DCD, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.IDENTIFIED), IdentificationConfiguration.INDTEL, IdentificationState.FINISHED),
        new TestCase(buildIndividuTelIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.DCD, AccessQuestionValue.NACC, SituationQuestionValue.ABSORBED, CategoryQuestionValue.SECONDARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.INDTEL, IdentificationState.FINISHED),
        new TestCase(buildIndividuTelIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.NOIDENT, AccessQuestionValue.ACC, SituationQuestionValue.NOORDINARY, CategoryQuestionValue.VACANT, OccupantQuestionValue.IDENTIFIED), IdentificationConfiguration.INDTEL, IdentificationState.FINISHED),
        new TestCase(buildIndividuTelIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.NOFIELD, AccessQuestionValue.NACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.INDTEL, IdentificationState.FINISHED),
        new TestCase(buildIndividuTelIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.SAME_ADDRESS, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.IDENTIFIED), IdentificationConfiguration.INDTEL, IdentificationState.FINISHED),
        new TestCase(buildIndividuTelIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.SAME_ADDRESS, AccessQuestionValue.ACC, null, CategoryQuestionValue.VACANT, OccupantQuestionValue.UNIDENTIFIED), IdentificationConfiguration.INDTEL, IdentificationState.ONGOING),
        new TestCase(buildIndividuTelIdentification(IdentificationQuestionValue.IDENTIFIED, null, AccessQuestionValue.NACC, SituationQuestionValue.ABSORBED, CategoryQuestionValue.SECONDARY, OccupantQuestionValue.IDENTIFIED), IdentificationConfiguration.INDTEL, IdentificationState.ONGOING),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.DCD, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.IDENTIFIED, null), IdentificationConfiguration.INDF2F, IdentificationState.FINISHED),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.UNIDENTIFIED, IndividualStatusQuestionValue.NOIDENT, AccessQuestionValue.NACC, SituationQuestionValue.NOORDINARY, CategoryQuestionValue.SECONDARY, OccupantQuestionValue.UNIDENTIFIED, null), IdentificationConfiguration.INDF2F, IdentificationState.FINISHED),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.DESTROY, IndividualStatusQuestionValue.NOFIELD, AccessQuestionValue.ACC, SituationQuestionValue.ABSORBED, CategoryQuestionValue.VACANT, OccupantQuestionValue.UNIDENTIFIED, null), IdentificationConfiguration.INDF2F, IdentificationState.FINISHED),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.SAME_ADDRESS, AccessQuestionValue.ACC, null, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED, null), IdentificationConfiguration.INDF2F, IdentificationState.ONGOING),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.SAME_ADDRESS, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.IDENTIFIED, null), IdentificationConfiguration.INDF2F, IdentificationState.FINISHED),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.OTHER_ADDRESS, AccessQuestionValue.ACC, SituationQuestionValue.NOORDINARY, CategoryQuestionValue.SECONDARY, null, null), IdentificationConfiguration.INDF2F, IdentificationState.ONGOING),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.OTHER_ADDRESS, AccessQuestionValue.NACC, SituationQuestionValue.ABSORBED, CategoryQuestionValue.VACANT, OccupantQuestionValue.UNIDENTIFIED, InterviewerCanProcessQuestionValue.NO), IdentificationConfiguration.INDF2F, IdentificationState.FINISHED),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.OTHER_ADDRESS, AccessQuestionValue.ACC, null, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED, InterviewerCanProcessQuestionValue.YES), IdentificationConfiguration.INDF2F, IdentificationState.ONGOING),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.SAME_ADDRESS, AccessQuestionValue.ACC, SituationQuestionValue.NOORDINARY, CategoryQuestionValue.SECONDARY, OccupantQuestionValue.UNIDENTIFIED, null), IdentificationConfiguration.INDF2F, IdentificationState.FINISHED),
        new TestCase(buildIndividuTelIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.OTHER_ADDRESS, AccessQuestionValue.ACC, SituationQuestionValue.ORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.IDENTIFIED), IdentificationConfiguration.INDTEL, IdentificationState.FINISHED),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.OTHER_ADDRESS, AccessQuestionValue.ACC, null, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED, InterviewerCanProcessQuestionValue.YES), IdentificationConfiguration.INDF2F, IdentificationState.ONGOING),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.OTHER_ADDRESS, AccessQuestionValue.ACC, SituationQuestionValue.NOORDINARY, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED, InterviewerCanProcessQuestionValue.NO), IdentificationConfiguration.INDF2F, IdentificationState.FINISHED),
        new TestCase(buildIndividuF2FIdentification(IdentificationQuestionValue.IDENTIFIED, IndividualStatusQuestionValue.OTHER_ADDRESS, AccessQuestionValue.ACC, SituationQuestionValue.ABSORBED, CategoryQuestionValue.PRIMARY, OccupantQuestionValue.UNIDENTIFIED, null), IdentificationConfiguration.INDF2F, IdentificationState.ONGOING)
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
      OccupantQuestionValue occupant,
      InterviewerCanProcessQuestionValue interview) {

    return Identification.builder()
        .individualStatus(individualStatus)
        .access(access)
        .situation(situation)
        .category(category)
        .occupant(occupant)
        .identification(identification)
        .interviewerCanProcess(interview)
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
