package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SurveyUnitClosingApiCsvPresenterTest {

    // Column layout — addRowWithTitleLabel prepends campaignLabel at index 0:
    // 0=campaignLabel 1=id 2=id 3=interviewerLabel 4=interviewerId
    // 5=ssech 6=identificationState 7=contactOutcome 8=questionnaireState 9=closingCauseType
    // All values serialised to String by CsvRow.from() via String.valueOf().
    //
    // PRODUCTION CONSTRAINT: List.of() rejects null elements.
    // contactOutcome (null when no candidate) and interviewerLabel
    // (null when both names are null) will NPE in toResponse().

    private SurveyUnitClosingApiCsvPresenter presenter;

    @BeforeEach
    void setup() {
        presenter = new SurveyUnitClosingApiCsvPresenter();
    }

    private ClosableSurveyUnitView buildProjection(String id, String campaignLabel,
                                                   String firstName, String lastName,
                                                   String interviewerId, Integer ssech,
                                                   ClosingCauseType closingCauseType) {
        ClosableSurveyUnitView p = mock(ClosableSurveyUnitView.class);
        when(p.getId()).thenReturn(id);
        when(p.getCampaignLabel()).thenReturn(campaignLabel);
        when(p.getInterviewerFirstName()).thenReturn(firstName);
        when(p.getInterviewerLastName()).thenReturn(lastName);
        when(p.getInterviewerId()).thenReturn(interviewerId);
        when(p.getSsech()).thenReturn(ssech);
        when(p.getClosingCauseType()).thenReturn(closingCauseType);
        // All identification fields null → toModelIdentification() returns null
        // → IdentificationState.getState(null, cfg) returns MISSING before the switch
        when(p.getIdentification()).thenReturn(null);
        when(p.getAccess()).thenReturn(null);
        when(p.getSituation()).thenReturn(null);
        when(p.getCategory()).thenReturn(null);
        when(p.getOccupant()).thenReturn(null);
        when(p.getIndividualStatus()).thenReturn(null);
        when(p.getInterviewerCanProcess()).thenReturn(null);
        when(p.getNumberOfRespondents()).thenReturn(null);
        when(p.getPresentInPreviousHome()).thenReturn(null);
        when(p.getHouseholdComposition()).thenReturn(null);
        when(p.getIdentificationType()).thenReturn(null);
        when(p.getCampaignIdentificationConfiguration()).thenReturn(IdentificationConfiguration.NOIDENT);
        return p;
    }

    private ClosableSurveyUnitView buildProjection(String id, String campaignLabel,
                                                   String firstName, String lastName,
                                                   String interviewerId, Integer ssech) {
        return buildProjection(id, campaignLabel, firstName, lastName,
                interviewerId, ssech, ClosingCauseType.values()[0]);
    }

    private ClosableSurveyUnitCandidateView buildCandidate(ContactOutcomeType type) {
        ClosableSurveyUnitCandidateView c = mock(ClosableSurveyUnitCandidateView.class);
        when(c.getContactOutcomeType()).thenReturn(type);
        return c;
    }

    private Map<String, ClosableSurveyUnitCandidateView> candidates(String id) {
        return Map.of(id, buildCandidate(ContactOutcomeType.INA));
    }

    private List<String> cells(SurveyUnitClosingCsv csv, int rowIndex) {
        return csv.rows().get(rowIndex).values();
    }

    @Test
    @DisplayName("empty() returns a SurveyUnitClosingCsv with no rows")
    void shouldReturnEmptyCsvWhenEmpty() {
        assertThat(presenter.empty().rows()).isEmpty();
    }

    @Test
    @DisplayName("Returns no rows when projection list is empty")
    void shouldReturnNoRowsWhenNoProjections() {
        SurveyUnitClosingCsv result = presenter.present(List.of(), Map.of(), Map.of());
        assertThat(result.rows()).isEmpty();
    }

    @Test
    @DisplayName("Produces one row per projection")
    void shouldProduceOneRowPerProjection() {
        ClosableSurveyUnitView p1 = buildProjection("ID-1", "Campaign A", "Alice", "Smith", "INT-1", 1);
        ClosableSurveyUnitView p2 = buildProjection("ID-2", "Campaign B", "Bob", "Jones", "INT-2", 2);

        SurveyUnitClosingCsv result = presenter.present(
                List.of(p1, p2),
                Map.of("ID-1", buildCandidate(ContactOutcomeType.INA),
                        "ID-2", buildCandidate(ContactOutcomeType.INA)),
                Map.of());

        assertThat(result.rows()).hasSize(2);
    }

    @Test
    @DisplayName("Each row has 10 columns (campaignLabel + 9 data columns)")
    void shouldHaveTenColumns() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("ID-1"), Map.of());

        assertThat(cells(result, 0)).hasSize(10);
    }

    @Test
    @DisplayName("Uses QUESTIONNAIRE_STATE_UNAVAILABLE when id is absent from state map")
    void shouldUseUnavailableStateWhenMissing() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("ID-1"), Map.of());

        assertThat(cells(result, 0).get(8)).isEqualTo(Constants.QUESTIONNAIRE_STATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("Uses questionnaire state from map when present")
    void shouldUseProvidedQuestionnaireState() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("ID-1"), Map.of("ID-1", "COMPLETED"));

        assertThat(cells(result, 0).get(8)).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("NPE when no candidate provided — contactOutcome null not accepted by List.of()")
    void shouldNpeWhenNoCandidate() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

        assertThatNullPointerException()
                .isThrownBy(() -> presenter.present(List.of(p), Map.of(), Map.of()));
    }

    @Test
    @DisplayName("Sets contactOutcome from candidate when present")
    void shouldSetContactOutcomeFromCandidate() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(
                List.of(p), Map.of("ID-1", buildCandidate(ContactOutcomeType.INA)), Map.of());

        assertThat(cells(result, 0).get(7)).isEqualTo("INA");
    }

    @Test
    @DisplayName("Concatenates first and last name with a space")
    void shouldConcatenateFirstAndLastName() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("ID-1"), Map.of());

        assertThat(cells(result, 0).get(3)).isEqualTo("Alice Smith");
    }

    @Test
    @DisplayName("NPE when both names are null — interviewerLabel null not accepted by List.of()")
    void shouldNpeWhenBothNamesNull() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", null, null, "INT-1", 1);

        assertThatNullPointerException()
                .isThrownBy(() -> presenter.present(List.of(p), candidates("ID-1"), Map.of()));
    }

    @Test
    @DisplayName("Uses only first name when last name is null")
    void shouldUseFirstNameOnlyWhenLastNameNull() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", "Alice", null, "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("ID-1"), Map.of());

        assertThat(cells(result, 0).get(3)).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Uses only last name when first name is null")
    void shouldUseLastNameOnlyWhenFirstNameNull() {
        ClosableSurveyUnitView p = buildProjection("ID-1", "Campaign", null, "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("ID-1"), Map.of());

        assertThat(cells(result, 0).get(3)).isEqualTo("Smith");
    }

    @Test
    @DisplayName("campaignLabel is at column 0, id at 1 and 2, interviewerId at 4, ssech at 5")
    void shouldRespectColumnOrder() {
        ClosableSurveyUnitView p = buildProjection("SU-42", "My Campaign", "Jean", "Dupont", "INT-99", 3);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-42"), Map.of());
        List<String> row = cells(result, 0);

        assertThat(row.get(0)).isEqualTo("My Campaign");
        assertThat(row.get(1)).isEqualTo("SU-42");
        assertThat(row.get(2)).isEqualTo("SU-42");
        assertThat(row.get(4)).isEqualTo("INT-99");
        assertThat(row.get(5)).isEqualTo("3");
    }

    @Test
    @DisplayName("identificationState is MISSING when all identification fields are null")
    void shouldHaveMissingIdentificationState() {
        ClosableSurveyUnitView p = buildProjection("SU-42", "Campaign", "Jean", "Dupont", "INT-99", 3);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-42"), Map.of());

        assertThat(cells(result, 0).get(6)).isEqualTo("MISSING");
    }

    @Test
    @DisplayName("closingCauseType is serialised to its enum name at column 9")
    void shouldHaveClosingCauseAtColumn9() {
        ClosingCauseType cause = ClosingCauseType.values()[0];
        ClosableSurveyUnitView p = buildProjection("SU-42", "Campaign", "Jean", "Dupont", "INT-99", 3, cause);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-42"), Map.of());

        assertThat(cells(result, 0).get(9)).isEqualTo(cause.name());
    }

    @Test
    @DisplayName("Rows appear in the same order as the input projections")
    void shouldPreserveProjectionOrder() {
        ClosableSurveyUnitView p1 = buildProjection("ID-1", "Campaign A", "Alice", "Smith", "INT-1", 1);
        ClosableSurveyUnitView p2 = buildProjection("ID-2", "Campaign B", "Bob", "Jones", "INT-2", 2);
        ClosableSurveyUnitView p3 = buildProjection("ID-3", "Campaign C", "Carol", "Brown", "INT-3", 3);

        SurveyUnitClosingCsv result = presenter.present(
                List.of(p1, p2, p3),
                Map.of("ID-1", buildCandidate(ContactOutcomeType.INA),
                        "ID-2", buildCandidate(ContactOutcomeType.INA),
                        "ID-3", buildCandidate(ContactOutcomeType.INA)),
                Map.of());

        assertThat(cells(result, 0).get(1)).isEqualTo("ID-1");
        assertThat(cells(result, 1).get(1)).isEqualTo("ID-2");
        assertThat(cells(result, 2).get(1)).isEqualTo("ID-3");
    }

    @Test
    @DisplayName("Each row resolves its own questionnaire state independently")
    void shouldResolveQuestionnaireStatePerRow() {
        ClosableSurveyUnitView p1 = buildProjection("ID-1", "Campaign", "Alice", "Smith", "INT-1", 1);
        ClosableSurveyUnitView p2 = buildProjection("ID-2", "Campaign", "Bob", "Jones", "INT-2", 2);

        SurveyUnitClosingCsv result = presenter.present(
                List.of(p1, p2),
                Map.of("ID-1", buildCandidate(ContactOutcomeType.INA),
                        "ID-2", buildCandidate(ContactOutcomeType.INA)),
                Map.of("ID-1", "COMPLETED"));

        assertThat(cells(result, 0).get(8)).isEqualTo("COMPLETED");
        assertThat(cells(result, 1).get(8)).isEqualTo(Constants.QUESTIONNAIRE_STATE_UNAVAILABLE);
    }
}