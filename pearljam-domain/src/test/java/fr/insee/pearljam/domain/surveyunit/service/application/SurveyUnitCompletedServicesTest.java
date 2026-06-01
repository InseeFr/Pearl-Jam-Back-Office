package fr.insee.pearljam.domain.surveyunit.service.application;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.domain.surveyunit.stub.SurveyUnitFetchPortStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitCompletedServiceTest {

    private SurveyUnitFetchPortStub surveyUnitFetchPortStub;
    private SurveyUnitCompletedService service;

    @BeforeEach
    void setUp() {
        surveyUnitFetchPortStub = new SurveyUnitFetchPortStub();
        service = new SurveyUnitCompletedService(surveyUnitFetchPortStub);
    }

    @Test
    @DisplayName("Queries fetch port with CLO and FIN states")
    void shouldQueryWithCloAndFinStates() {
        surveyUnitFetchPortStub.willReturn(List.of());

        service.getCompletedSurveyUnits("campaign-01", surveyUnits -> surveyUnits);

        assertThat(surveyUnitFetchPortStub.getCapturedStateTypes())
                .containsExactlyInAnyOrder(StateType.CLO, StateType.FIN);
    }

    @Test
    @DisplayName("Forwards campaign id to the fetch port")
    void shouldForwardCampaignId_toFetchPort() {
        surveyUnitFetchPortStub.willReturn(List.of());

        service.getCompletedSurveyUnits("campaign-01", surveyUnits -> surveyUnits);

        assertThat(surveyUnitFetchPortStub.getCapturedCampaignId()).isEqualTo("campaign-01");
    }

    @Test
    @DisplayName("Returns result transformed by presenter")
    void shouldReturnPresentedResult() {
        SurveyUnitFetchedByStatesAndCampaignIdView su1 = surveyUnitView("su-1");
        SurveyUnitFetchedByStatesAndCampaignIdView su2 = surveyUnitView("su-2");
        surveyUnitFetchPortStub.willReturn(List.of(su1, su2));

        List<SurveyUnitFetchedByStatesAndCampaignIdView> result =
                service.getCompletedSurveyUnits("campaign-01", surveyUnits -> surveyUnits);

        assertThat(result).containsExactly(su1, su2);
    }

    @Test
    @DisplayName("Returns empty list when no completed survey units found")
    void shouldReturnEmptyList_whenNoSurveyUnitsFound() {
        surveyUnitFetchPortStub.willReturn(List.of());

        List<SurveyUnitFetchedByStatesAndCampaignIdView> result =
                service.getCompletedSurveyUnits("campaign-empty", surveyUnits -> surveyUnits);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Delegates result to presenter and returns its output")
    void shouldDelegateToPresenter() {
        surveyUnitFetchPortStub.willReturn(List.of(surveyUnitView("su-1"), surveyUnitView("su-2")));

        int result = service.getCompletedSurveyUnits("campaign-01", List::size);

        assertThat(result).isEqualTo(2);
    }

    // --- helpers ---

    private SurveyUnitFetchedByStatesAndCampaignIdView surveyUnitView(String id) {
        return new SurveyUnitFetchedByStatesAndCampaignIdView() {
            @Override public String getSurveyUnitId() { return id; }
            @Override public String getSurveyUnitDisplayName() { return "Display " + id; }
            @Override public String getInterviewerFirstName() { return "John"; }
            @Override public String getInterviewerLastName() { return "Doe"; }
            @Override public String getEndDate() { return "2024-01-01"; }
            @Override public String getContactOutcome() { return "INA"; }
            @Override public String getClosingCauseType() { return "NPI"; }
            @Override public Boolean getViewed() { return false; }
            @Override public String getComment() { return "A comment"; }
        };
    }
}