package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitCompletedPageResponse;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitCompletedApiPresenterTest {

    private SurveyUnitCompletedApiPresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = new SurveyUnitCompletedApiPresenter();
        ReflectionTestUtils.setField(presenter, "datacollectionUiUrl", "https://datacollection.example.com");
    }

    @Test
@DisplayName("Returns empty list when no survey units provided")
    void shouldReturnEmptyList_whenNoSurveyUnits() {
        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of()));

        assertThat(result.content()).isEmpty();
    }

    @Test
    @DisplayName("Maps survey unit id and display name")
    void shouldMapSurveyUnitIdAndDisplayName() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", "INA", "NPI", false, "A comment");

        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of(su)));

        assertThat(result.content().getFirst().surveyUnitId()).isEqualTo("su-1");
        assertThat(result.content().getFirst().surveyUnitDisplayName()).isEqualTo("Survey 1");
    }

    @Test
    @DisplayName("Concatenates interviewer first and last name with a space")
    void shouldConcatenateInterviewerFirstAndLastName() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", "INA", "NPI", false, "A comment");

        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of(su)));

        assertThat(result.content().getFirst().interviewerLabel()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Converts contactOutcome string to enum")
    void shouldConvertContactOutcome_toEnum() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", "INA", "NPI", false, "A comment");

        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of(su)));

        assertThat(result.content().getFirst().contactOutcome()).isEqualTo(ContactOutcomeType.INA);
    }

    @Test
    @DisplayName("Sets contactOutcome to null when source value is null")
    void shouldSetContactOutcomeToNull_whenSourceIsNull() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", null, "NPI", false, "A comment");

        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of(su)));

        assertThat(result.content().getFirst().contactOutcome()).isNull();
    }

    @Test
    @DisplayName("Converts closingCauseType string to enum")
    void shouldConvertClosingCauseType_toEnum() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", "INA", "NPI", false, "A comment");

        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of(su)));

        assertThat(result.content().getFirst().closingCauseType()).isEqualTo(ClosingCauseType.NPI);
    }

    @Test
    @DisplayName("Sets closingCauseType to null when source value is null")
    void shouldSetClosingCauseTypeToNull_whenSourceIsNull() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", "INA", null, false, "A comment");

        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of(su)));

        assertThat(result.content().getFirst().closingCauseType()).isNull();
    }

    @Test
    @DisplayName("Builds readOnlyUrl from datacollectionUiUrl and survey unit id")
    void shouldBuildReadOnlyUrl_fromBaseUrlAndSurveyUnitId() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", "INA", "NPI", false, "A comment");

        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of(su)));

        assertThat(result.content().getFirst().readOnlyUrl())
                .isEqualTo("https://datacollection.example.com/review/interrogations/su-1");
    }

    @Test
    @DisplayName("Maps all remaining fields correctly")
    void shouldMapRemainingFields() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", "INA", "NPI", true, "My comment");

        SurveyUnitCompletedPageResponse result = presenter.present(new PageImpl<>(List.of(su)));

        assertThat(result.content().getFirst().endDate()).isEqualTo("2024-01-15");
        assertThat(result.content().getFirst().viewed()).isTrue();
        assertThat(result.content().getFirst().comment()).isEqualTo("My comment");
    }

    @Test
    @DisplayName("Presents multiple survey units preserving order")
    void shouldPresentMultipleSurveyUnits_preservingOrder() {
        SurveyUnitFetchedByStatesAndCampaignIdView su1 = surveyUnitView("su-1", "Survey 1", "John", "Doe",
                "2024-01-15", "INA", "NPI", false, null);
        SurveyUnitFetchedByStatesAndCampaignIdView su2 = surveyUnitView("su-2", "Survey 2", "Jane", "Smith",
                "2024-01-16", null, null, true, "comment");

        SurveyUnitCompletedPageResponse results = presenter.present(new PageImpl<>(List.of(su1, su2)));

        assertThat(results.totalElements()).isEqualTo(2);
        assertThat(results.content().getFirst().surveyUnitId()).isEqualTo("su-1");
        assertThat(results.content().get(1).surveyUnitId()).isEqualTo("su-2");
    }

    // --- helpers ---

    private SurveyUnitFetchedByStatesAndCampaignIdView surveyUnitView(
            String id, String displayName,
            String firstName, String lastName,
            String endDate, String contactOutcome,
            String closingCauseType, Boolean viewed, String comment) {
        return new SurveyUnitFetchedByStatesAndCampaignIdView(
            id,
            displayName,
            firstName,
            lastName,
            endDate,
            contactOutcome,
            closingCauseType,
            viewed,
            comment
        );
    }
}