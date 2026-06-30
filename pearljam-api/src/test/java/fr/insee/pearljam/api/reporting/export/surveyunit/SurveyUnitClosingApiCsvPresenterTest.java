package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitClosingViewModelMapper;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SurveyUnitClosingApiCsvPresenterTest {

    private SurveyUnitClosingApiCsvPresenter presenter;

    @BeforeEach
    void setup() {
        presenter = new SurveyUnitClosingApiCsvPresenter(new SurveyUnitClosingViewModelMapper());
    }

    private ClosableSurveyUnitView buildProjection(String campaignLabel, String surveyUnitId,
                                                   String firstName, String lastName,
                                                   String interviewerId, Integer ssech,
                                                   ClosingCauseType closingCauseType) {
        ClosableSurveyUnitView p = mock(ClosableSurveyUnitView.class);
        when(p.getId()).thenReturn(surveyUnitId);
        when(p.getDisplayName()).thenReturn(surveyUnitId + "_DISPLAY_NAME");
        when(p.getCampaignLabel()).thenReturn(campaignLabel);
        when(p.getInterviewerFirstName()).thenReturn(firstName);
        when(p.getInterviewerLastName()).thenReturn(lastName);
        when(p.getInterviewerId()).thenReturn(interviewerId);
        when(p.getSsech()).thenReturn(ssech);
        when(p.getClosingCauseType()).thenReturn(closingCauseType);
        // "75001 PARIS" -> departement = substring(0,2) = "75"
        //               -> city        = substring(6)   = "PARIS"
        when(p.getAddressL6()).thenReturn("75001 PARIS");
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

    private ClosableSurveyUnitView buildProjection(String campaignLabel, String surveyUnitId,
                                                   String firstName, String lastName,
                                                   String interviewerId, Integer ssech) {
        return buildProjection(campaignLabel, surveyUnitId, firstName, lastName,
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
        ClosableSurveyUnitView p1 = buildProjection("Campaign A", "SU-A", "Alice", "Smith", "INT-1", 1);
        ClosableSurveyUnitView p2 = buildProjection( "Campaign B", "SU-B","Bob", "Jones", "INT-2", 2);

        SurveyUnitClosingCsv result = presenter.present(
                List.of(p1, p2),
                Map.of("SU-A", buildCandidate(ContactOutcomeType.INA),
                        "SU-B", buildCandidate(ContactOutcomeType.INA)),
                Map.of());

        assertThat(result.rows()).hasSize(2);
    }

    @Test
    @DisplayName("Each row has 12 columns (campaignLabel + 11 data columns)")
    void shouldHaveTwelveColumns() {
        ClosableSurveyUnitView p = buildProjection("Campaign","SU-A", "Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-A"), Map.of());

        assertThat(cells(result, 0)).hasSize(12);
    }

    @Test
    @DisplayName("Uses QUESTIONNAIRE_STATE_UNAVAILABLE when id is absent from state map")
    void shouldUseUnavailableStateWhenMissing() {
        ClosableSurveyUnitView p = buildProjection("Campaign","SU-A", "Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-A"), Map.of());

        assertThat(cells(result, 0).get(10)).isEqualTo(Constants.QUESTIONNAIRE_STATE_UNAVAILABLE);
    }

    @Test
    @DisplayName("Uses questionnaire state from map when present")
    void shouldUseProvidedQuestionnaireState() {
        ClosableSurveyUnitView p = buildProjection("Campaign", "SU-A","Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-A"), Map.of("SU-A", "COMPLETED"));

        assertThat(cells(result, 0).get(10)).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("Sets contactOutcome from candidate when present")
    void shouldSetContactOutcomeFromCandidate() {
        ClosableSurveyUnitView p = buildProjection("Campaign", "SU-A","Alice", "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(
                List.of(p), Map.of("SU-A", buildCandidate(ContactOutcomeType.INA)), Map.of());

        assertThat(cells(result, 0).get(9)).isEqualTo("INA");
    }

    @Test
    @DisplayName("Concatenates first and last name with a space")
    void shouldConcatenateFirstAndLastName() {
        ClosableSurveyUnitView p = buildProjection("Campaign", "SU-A", "Alice","Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-A"), Map.of());

        assertThat(cells(result, 0).get(3)).isEqualTo("Alice Smith");
    }

    @Test
    @DisplayName("Uses only first name when last name is null")
    void shouldUseFirstNameOnlyWhenLastNameNull() {
        ClosableSurveyUnitView p = buildProjection("Campaign", "SU-A","Alice", null, "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-A"), Map.of());

        assertThat(cells(result, 0).get(3)).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Uses only last name when first name is null")
    void shouldUseLastNameOnlyWhenFirstNameNull() {
        ClosableSurveyUnitView p = buildProjection("Campaign", "SU-1",null, "Smith", "INT-1", 1);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-1"), Map.of());

        assertThat(cells(result, 0).get(3)).isEqualTo("Smith");
    }

    @Test
    @DisplayName("campaignLabel at 0, id at 1/2, interviewerId at 4, ssech at 5, departement at 6, city at 7")
    void shouldRespectColumnOrder() {
        ClosableSurveyUnitView p = buildProjection("My Campaign","SU-A", "Jean", "Dupont", "INT-99", 3);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-A"), Map.of());
        List<String> row = cells(result, 0);

        assertThat(row.get(0)).isEqualTo("My Campaign");
        assertThat(row.get(1)).isEqualTo("SU-A_DISPLAY_NAME");
        assertThat(row.get(2)).isEqualTo("SU-A");
        assertThat(row.get(4)).isEqualTo("INT-99");
        assertThat(row.get(5)).isEqualTo("3");
        assertThat(row.get(6)).isEqualTo("75");
        assertThat(row.get(7)).isEqualTo("PARIS");
    }

    @Test
    @DisplayName("identificationState is MISSING when all identification fields are null")
    void shouldHaveMissingIdentificationState() {
        ClosableSurveyUnitView p = buildProjection("Campaign", "SU-1", "Jean", "Dupont", "INT-99", 3);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-1"), Map.of());

        assertThat(cells(result, 0).get(8)).isEqualTo("MISSING");
    }

    @Test
    @DisplayName("closingCauseType is serialised to its enum name at column 11")
    void shouldHaveClosingCauseAtColumn11() {
        ClosingCauseType cause = ClosingCauseType.values()[0];
        ClosableSurveyUnitView p = buildProjection("Campaign", "SU-1", "Jean", "Dupont", "INT-99", 3, cause);

        SurveyUnitClosingCsv result = presenter.present(List.of(p), candidates("SU-1"), Map.of());

        assertThat(cells(result, 0).get(11)).isEqualTo(cause.name());
    }

    @Test
    @DisplayName("Rows appear in the same order as the input projections")
    void shouldPreserveProjectionOrder() {
        ClosableSurveyUnitView p1 = buildProjection("Campaign A", "SU-A", "Alice", "Smith", "INT-1", 1);
        ClosableSurveyUnitView p2 = buildProjection( "Campaign B", "SU-B","Bob", "Jones", "INT-2", 2);
        ClosableSurveyUnitView p3 = buildProjection("Campaign C", "SU-C","Carol", "Brown", "INT-3", 3);

        SurveyUnitClosingCsv result = presenter.present(
                List.of(p1, p2, p3),
                Map.of("SU-A", buildCandidate(ContactOutcomeType.INA),
                        "SU-B", buildCandidate(ContactOutcomeType.INA),
                        "SU-C", buildCandidate(ContactOutcomeType.INA)),
                Map.of());

        assertThat(cells(result, 0).getFirst()).isEqualTo("Campaign A");
        assertThat(cells(result, 1).getFirst()).isEqualTo("Campaign B");
        assertThat(cells(result, 2).getFirst()).isEqualTo("Campaign C");
    }

    @Test
    @DisplayName("Each row resolves its own questionnaire state independently")
    void shouldResolveQuestionnaireStatePerRow() {
        ClosableSurveyUnitView p1 = buildProjection("Campaign", "SU-A", "Alice", "Smith", "INT-1", 1);
        ClosableSurveyUnitView p2 = buildProjection( "Campaign", "SU-B", "Bob", "Jones", "INT-2", 2);

        SurveyUnitClosingCsv result = presenter.present(
                List.of(p1, p2),
                Map.of("SU-A", buildCandidate(ContactOutcomeType.INA),
                        "SU-B", buildCandidate(ContactOutcomeType.INA)),
                Map.of("SU-A", "COMPLETED"));

        assertThat(cells(result, 0).get(10)).isEqualTo("COMPLETED");
        assertThat(cells(result, 1).get(10)).isEqualTo(Constants.QUESTIONNAIRE_STATE_UNAVAILABLE);
    }
}