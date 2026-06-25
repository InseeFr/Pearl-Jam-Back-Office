package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SurveyUnitClosingApiCsvPresenter")
class SurveyUnitClosingApiCsvPresenterTest {

    // Column indices AFTER prepended campaignLabel:
    // 0  = campaignLabel  (prepended by addRowWithTitleLabel)
    // 1  = id
    // 2  = id (repeated)
    // 3  = interviewerLabel
    // 4  = interviewerId
    // 5  = ssech
    // 6  = identificationState
    // 7  = contactOutcome
    // 8  = questionnaireState
    // 9  = closingCauseType
    private static final int COL_CAMPAIGN_LABEL      = 0;
    private static final int COL_ID_1                = 1;
    private static final int COL_ID_2                = 2;
    private static final int COL_INTERVIEWER_LABEL   = 3;
    private static final int COL_INTERVIEWER_ID      = 4;
    private static final int COL_SSECH               = 5;
    private static final int COL_IDENTIFICATION_STATE = 6;
    private static final int COL_CONTACT_OUTCOME     = 7;
    private static final int COL_QUESTIONNAIRE_STATE = 8;
    private static final int COL_CLOSING_CAUSE       = 9;

    private static final int TOTAL_COLUMNS = 10;

    private SurveyUnitClosingApiCsvPresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = new SurveyUnitClosingApiCsvPresenter();
    }

    // ------------------------------------------------------------------
    // Helper: build a fully stubbed projection.
    // IdentificationConfiguration.NOIDENT is used as a safe default:
    // when all identification fields are null, toModelIdentification()
    // returns null, so getState(null, config) returns MISSING immediately
    // without entering the switch — so any non-null config works here.
    // ------------------------------------------------------------------
    private ClosableSurveyUnitView buildProjection(
            String id,
            String campaignLabel,
            String firstName,
            String lastName,
            String interviewerId,
            Integer ssech) {

        ClosableSurveyUnitView projection = mock(ClosableSurveyUnitView.class);
        when(projection.getId()).thenReturn(id);
        when(projection.getCampaignLabel()).thenReturn(campaignLabel);
        when(projection.getInterviewerFirstName()).thenReturn(firstName);
        when(projection.getInterviewerLastName()).thenReturn(lastName);
        when(projection.getInterviewerId()).thenReturn(interviewerId);
        when(projection.getSsech()).thenReturn(ssech);
        when(projection.getClosingCauseType()).thenReturn(null);

        // All identification fields null → toModelIdentification() returns null
        // → getState(null, config) returns MISSING without touching the switch
        when(projection.getIdentification()).thenReturn(null);
        when(projection.getAccess()).thenReturn(null);
        when(projection.getSituation()).thenReturn(null);
        when(projection.getCategory()).thenReturn(null);
        when(projection.getOccupant()).thenReturn(null);
        when(projection.getIndividualStatus()).thenReturn(null);
        when(projection.getInterviewerCanProcess()).thenReturn(null);
        when(projection.getNumberOfRespondents()).thenReturn(null);
        when(projection.getPresentInPreviousHome()).thenReturn(null);
        when(projection.getHouseholdComposition()).thenReturn(null);
        when(projection.getIdentificationType()).thenReturn(null);
        // Must be non-null so the switch in IdentificationState.getState() is not reached
        // (identification is null, so MISSING is returned before the switch)
        when(projection.getCampaignIdentificationConfiguration())
                .thenReturn(IdentificationConfiguration.NOIDENT);

        return projection;
    }

    private ClosableSurveyUnitCandidateView buildCandidate(ContactOutcomeType type) {
        ClosableSurveyUnitCandidateView candidate = mock(ClosableSurveyUnitCandidateView.class);
        when(candidate.getContactOutcomeType()).thenReturn(type);
        return candidate;
    }

    // All values in CsvRow are stored as Strings (CsvRow.from() calls String.valueOf())
    private List<String> cellsOf(SurveyUnitClosingCsv csv, int rowIndex) {
        return csv.rows().get(rowIndex).values();
    }

    // ------------------------------------------------------------------
    // empty()
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("empty()")
    class EmptyMethod {

        @Test
        @DisplayName("returns a SurveyUnitClosingCsv with an empty row list")
        void shouldReturnEmptyCsv() {
            SurveyUnitClosingCsv result = presenter.empty();
            assertThat(result).isNotNull();
            assertThat(result.rows()).isEmpty();
        }
    }

    // ------------------------------------------------------------------
    // present() – row count
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("present() – row production")
    class PresentMethod {

        @Test
        @DisplayName("returns an empty CSV when projection list is empty")
        void shouldReturnEmptyRowsWhenNoProjections() {
            SurveyUnitClosingCsv result = presenter.present(List.of(), Map.of(), Map.of());
            assertThat(result.rows()).isEmpty();
        }

        @Test
        @DisplayName("produces one row per projection")
        void shouldProduceOneRowPerProjection() {
            ClosableSurveyUnitView p1 = buildProjection("ID-1", "Campaign A", "Alice", "Smith", "INT-1", 1);
            ClosableSurveyUnitView p2 = buildProjection("ID-2", "Campaign B", "Bob",   "Jones", "INT-2", 2);

            SurveyUnitClosingCsv result = presenter.present(List.of(p1, p2), Map.of(), Map.of());

            assertThat(result.rows()).hasSize(2);
        }

        @Test
        @DisplayName("each row has exactly " + TOTAL_COLUMNS + " columns (campaignLabel + 9 data columns)")
        void shouldHaveTenColumns() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "A", "B", "INT-1", 1);
            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0)).hasSize(TOTAL_COLUMNS);
        }

        // ------------------------------------------------------------------
        // questionnaire state
        // ------------------------------------------------------------------
        @Test
        @DisplayName("uses QUESTIONNAIRE_STATE_UNAVAILABLE when state is absent from map")
        void shouldUseUnavailableStateWhenMissing() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0))
                    .element(COL_QUESTIONNAIRE_STATE)
                    .isEqualTo(Constants.QUESTIONNAIRE_STATE_UNAVAILABLE);
        }

        @Test
        @DisplayName("uses questionnaire state from map when present")
        void shouldUseProvidedQuestionnaireState() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

            SurveyUnitClosingCsv result = presenter.present(
                    List.of(p), Map.of(), Map.of("ID-1", "COMPLETED"));

            assertThat(cellsOf(result, 0))
                    .element(COL_QUESTIONNAIRE_STATE)
                    .isEqualTo("COMPLETED");
        }

        // ------------------------------------------------------------------
        // contact outcome
        // CsvRow.from() calls String.valueOf() on every value,
        // so null → "" and ContactOutcomeType.INA → "INA"
        // ------------------------------------------------------------------
        @Test
        @DisplayName("sets contactOutcome to empty string when candidate is absent")
        void shouldSetContactOutcomeEmptyWhenCandidateAbsent() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0))
                    .element(COL_CONTACT_OUTCOME)
                    .isEqualTo("");  // null → "" via String.valueOf()
        }

        @Test
        @DisplayName("sets contactOutcome from candidate when candidate is present")
        void shouldSetContactOutcomeFromCandidate() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);
            ClosableSurveyUnitCandidateView candidate = buildCandidate(ContactOutcomeType.INA);

            SurveyUnitClosingCsv result = presenter.present(
                    List.of(p), Map.of("ID-1", candidate), Map.of());

            assertThat(cellsOf(result, 0))
                    .element(COL_CONTACT_OUTCOME)
                    .isEqualTo("INA");  // enum.toString() via String.valueOf()
        }
    }

    // ------------------------------------------------------------------
    // Interviewer label building
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Interviewer label")
    class InterviewerLabel {

        @Test
        @DisplayName("concatenates first and last name with a space")
        void shouldConcatenateFirstAndLastName() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0))
                    .element(COL_INTERVIEWER_LABEL)
                    .isEqualTo("Alice Smith");
        }

        @Test
        @DisplayName("returns empty string when both first and last name are null")
        void shouldReturnEmptyStringWhenBothNamesNull() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", null, null, "INT-1", 1);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0))
                    .element(COL_INTERVIEWER_LABEL)
                    .isEqualTo("");  // null → "" via CsvRow.from()
        }

        @Test
        @DisplayName("uses only first name when last name is null")
        void shouldUseFirstNameOnlyWhenLastNameNull() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", null, "INT-1", 1);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0))
                    .element(COL_INTERVIEWER_LABEL)
                    .isEqualTo("Alice");
        }

        @Test
        @DisplayName("uses only last name when first name is null")
        void shouldUseLastNameOnlyWhenFirstNameNull() {
            ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", null, "Smith", "INT-1", 1);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0))
                    .element(COL_INTERVIEWER_LABEL)
                    .isEqualTo("Smith");
        }
    }

    // ------------------------------------------------------------------
    // Row column order and content
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Row column order")
    class RowColumnOrder {

        @Test
        @DisplayName("campaignLabel is prepended at index 0")
        void shouldHaveCampaignLabelAtIndex0() {
            ClosableSurveyUnitView p = buildProjection("SU-42", "My Campaign", "Jean", "Dupont", "INT-99", 3);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0)).element(COL_CAMPAIGN_LABEL).isEqualTo("My Campaign");
        }

        @Test
        @DisplayName("id appears at columns 1 and 2")
        void shouldHaveIdAtColumns1And2() {
            ClosableSurveyUnitView p = buildProjection("SU-42", "Campaign", "Jean", "Dupont", "INT-99", 3);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());
            List<String> cells = cellsOf(result, 0);

            assertThat(cells).element(COL_ID_1).isEqualTo("SU-42");
            assertThat(cells).element(COL_ID_2).isEqualTo("SU-42");
        }

        @Test
        @DisplayName("interviewerId is at column 4")
        void shouldHaveInterviewerIdAtColumn4() {
            ClosableSurveyUnitView p = buildProjection("SU-42", "Campaign", "Jean", "Dupont", "INT-99", 3);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0)).element(COL_INTERVIEWER_ID).isEqualTo("INT-99");
        }

        @Test
        @DisplayName("ssech is at column 5")
        void shouldHaveSsechAtColumn5() {
            ClosableSurveyUnitView p = buildProjection("SU-42", "Campaign", "Jean", "Dupont", "INT-99", 3);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0)).element(COL_SSECH).isEqualTo("3");
        }

        @Test
        @DisplayName("identificationState is MISSING when all identification fields are null")
        void shouldHaveMissingIdentificationStateWhenAllNull() {
            ClosableSurveyUnitView p = buildProjection("SU-42", "Campaign", "Jean", "Dupont", "INT-99", 3);

            SurveyUnitClosingCsv result = presenter.present(List.of(p), Map.of(), Map.of());

            assertThat(cellsOf(result, 0))
                    .element(COL_IDENTIFICATION_STATE)
                    .isEqualTo("MISSING");
        }
    }

    // ------------------------------------------------------------------
    // Multiple projections preserve order
    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Multiple projections")
    class MultipleProjections {

        @Test
        @DisplayName("rows appear in the same order as the input projections")
        void shouldPreserveProjectionOrder() {
            ClosableSurveyUnitView p1 = buildProjection("ID-1", "Campaign A", "Alice", "Smith", "INT-1", 1);
            ClosableSurveyUnitView p2 = buildProjection("ID-2", "Campaign B", "Bob",   "Jones", "INT-2", 2);
            ClosableSurveyUnitView p3 = buildProjection("ID-3", "Campaign C", "Carol", "Brown", "INT-3", 3);

            SurveyUnitClosingCsv result = presenter.present(
                    List.of(p1, p2, p3), Map.of(), Map.of());

            assertThat(cellsOf(result, 0)).element(COL_ID_1).isEqualTo("ID-1");
            assertThat(cellsOf(result, 1)).element(COL_ID_1).isEqualTo("ID-2");
            assertThat(cellsOf(result, 2)).element(COL_ID_1).isEqualTo("ID-3");
        }

        @Test
        @DisplayName("each row resolves its own questionnaire state independently")
        void shouldResolveQuestionnaireStatePerRow() {
            ClosableSurveyUnitView p1 = buildProjection("ID-1", "Campaign", "A", "B", "INT-1", 1);
            ClosableSurveyUnitView p2 = buildProjection("ID-2", "Campaign", "C", "D", "INT-2", 2);

            SurveyUnitClosingCsv result = presenter.present(
                    List.of(p1, p2),
                    Map.of(),
                    Map.of("ID-1", "COMPLETED"));   // only ID-1 has a state

            assertThat(cellsOf(result, 0)).element(COL_QUESTIONNAIRE_STATE).isEqualTo("COMPLETED");
            assertThat(cellsOf(result, 1)).element(COL_QUESTIONNAIRE_STATE)
                    .isEqualTo(Constants.QUESTIONNAIRE_STATE_UNAVAILABLE);
        }
    }
}