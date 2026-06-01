package fr.insee.pearljam.domain.surveyunit.service.application;


import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.campaign.service.dummy.SurveyUnitRepositoryStub;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.campaign.stub.CampaignRepositoryStub;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.domain.surveyunit.service.SurveyUnitFetchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyUnitFetchServiceTest {

    private SurveyUnitRepositoryStub surveyUnitRepositoryStub;
    private SurveyUnitFetchService service;

    private final String campaignIdToTest = "campaign-01";

    @BeforeEach
    void setUp() {
        surveyUnitRepositoryStub = new SurveyUnitRepositoryStub();
        CampaignRepositoryStub campaignRepositoryStub = new CampaignRepositoryStub(generateCampaign());
        service = new SurveyUnitFetchService(surveyUnitRepositoryStub, campaignRepositoryStub);
    }

    @Test
    @DisplayName("Throws CampaignNotFoundException when campaign does not exist")
    void shouldThrowCampaignNotFoundException_whenCampaignDoesNotExist() {
        List<StateType> states = List.of(StateType.CLO);
        assertThatThrownBy(() -> service.getSurveyUnitsByStatesAndCampaignId(states, "unknown-campaign"))
                .isInstanceOf(CampaignNotFoundExceptionRuntime.class);
    }

    @Test
    @DisplayName("Does not call survey unit repository when campaign does not exist")
    void shouldNotCallSurveyUnitRepository_whenCampaignDoesNotExist() {
        List<StateType> states = List.of(StateType.CLO);
        assertThatThrownBy(() -> service.getSurveyUnitsByStatesAndCampaignId(states, "unknown-campaign"))
                .isInstanceOf(CampaignNotFoundExceptionRuntime.class);

        assertThat(surveyUnitRepositoryStub.getCapturedCampaignId()).isNull();
    }

    @Test
    @DisplayName("Returns survey units when campaign exists")
    void shouldReturnSurveyUnits_whenCampaignExists() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitCompletedView();
        surveyUnitRepositoryStub.willReturn(List.of(su));

        List<SurveyUnitFetchedByStatesAndCampaignIdView> result = service.getSurveyUnitsByStatesAndCampaignId(List.of(StateType.CLO, StateType.FIN), campaignIdToTest);

        assertThat(result).containsExactly(su);
    }

    @Test
    @DisplayName("Forwards state types to the repository")
    void shouldForwardStateTypes_toRepository() {
        surveyUnitRepositoryStub.willReturn(List.of());
        List<StateType> stateTypes = List.of(StateType.CLO, StateType.FIN);

        service.getSurveyUnitsByStatesAndCampaignId(stateTypes, campaignIdToTest);

        assertThat(surveyUnitRepositoryStub.getCapturedStateTypes())
                .containsExactlyInAnyOrder(StateType.CLO, StateType.FIN);
    }

    @Test
    @DisplayName("Forwards campaign id to the repository")
    void shouldForwardCampaignId_toRepository() {
        surveyUnitRepositoryStub.willReturn(List.of());

        service.getSurveyUnitsByStatesAndCampaignId(List.of(StateType.FIN), campaignIdToTest);

        assertThat(surveyUnitRepositoryStub.getCapturedCampaignId()).isEqualTo(campaignIdToTest);
    }

    @Test
    @DisplayName("Returns empty list when campaign exists but has no completed survey units")
    void shouldReturnEmptyList_whenNoSurveyUnitsFound() {
        surveyUnitRepositoryStub.willReturn(List.of());

        List<SurveyUnitFetchedByStatesAndCampaignIdView> result = service.getSurveyUnitsByStatesAndCampaignId(List.of(StateType.CLO), campaignIdToTest);

        assertThat(result).isEmpty();
    }

    // --- helpers ---

    private HashMap<String, CampaignSummary> generateCampaign()
    {
        HashMap<String, CampaignSummary> campaigns = new HashMap<>();
        campaigns.put(campaignIdToTest, null);
        return campaigns;
    }

    private SurveyUnitFetchedByStatesAndCampaignIdView surveyUnitCompletedView() {
        return new SurveyUnitFetchedByStatesAndCampaignIdView() {
            @Override public String getSurveyUnitId() { return "su-1"; }
            @Override public String getSurveyUnitDisplayName() { return "Display " + "su-1"; }
            @Override public String getInterviewerFirstName() { return "John"; }
            @Override public String getInterviewerLastName() { return "Doe"; }
            @Override public String getEndDate() { return "2024-01-01"; }
            @Override public String getContactOutcome() { return "INA"; }
            @Override public String getClosingCauseType() { return "NPI"; }
            @Override public Boolean getRead() { return false; }
            @Override public String getReadOnlyUrl() { return "https://example.com/" + "su-1"; }
            @Override public String getComment() { return "A comment"; }
        };
    }
}