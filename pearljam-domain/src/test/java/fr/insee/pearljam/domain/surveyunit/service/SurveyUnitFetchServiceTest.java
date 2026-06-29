package fr.insee.pearljam.domain.surveyunit.service;


import fr.insee.pearljam.domain.campaign.readmodel.CampaignSummary;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundExceptionRuntime;
import fr.insee.pearljam.domain.campaign.stub.CampaignRepositoryStub;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
import fr.insee.pearljam.domain.surveyunit.stub.SurveyUnitFetchedByStatesRepositoryStub;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.domain.user.stub.UserServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyUnitFetchServiceTest {

    private SurveyUnitFetchedByStatesRepositoryStub surveyUnitFetchedByStatesRepositoryPort;
    private SurveyUnitFetchService service;

    private final String campaignIdToTest = "campaign-01";

    @BeforeEach
    void setUp() {
        surveyUnitFetchedByStatesRepositoryPort = new SurveyUnitFetchedByStatesRepositoryStub();
        CampaignRepositoryStub campaignRepositoryStub = new CampaignRepositoryStub(generateCampaign());
        UserService userService = new UserServiceStub(List.of());
        service = new SurveyUnitFetchService(surveyUnitFetchedByStatesRepositoryPort, campaignRepositoryStub, userService);
    }

    @Test
    @DisplayName("Throws CampaignNotFoundException when campaign does not exist")
    void shouldThrowCampaignNotFoundException_whenCampaignDoesNotExist() {
        List<StateType> states = List.of(StateType.CLO);
        assertThatThrownBy(() -> service.getSurveyUnitsByStatesAndCampaignId(null, states, "unknown-campaign", null, null, null))
                .isInstanceOf(CampaignNotFoundExceptionRuntime.class);
    }

    @Test
    @DisplayName("Returns survey units when campaign exists")
    void shouldReturnSurveyUnits_whenCampaignExists() {
        SurveyUnitFetchedByStatesAndCampaignIdView su = surveyUnitCompletedView();
        surveyUnitFetchedByStatesRepositoryPort.willReturn(new PageImpl<>(List.of(su)));

        Page<SurveyUnitFetchedByStatesAndCampaignIdView> result = service.getSurveyUnitsByStatesAndCampaignId(null, List.of(StateType.CLO, StateType.FIN), campaignIdToTest, null, null, null);

        assertThat(result).containsExactly(su);
    }

    @Test
    @DisplayName("Forwards state types to the repository")
    void shouldForwardStateTypes_toRepository() {
        surveyUnitFetchedByStatesRepositoryPort.willReturn(new PageImpl<>(List.of()));
        List<StateType> stateTypes = List.of(StateType.CLO, StateType.FIN);

        service.getSurveyUnitsByStatesAndCampaignId(null, stateTypes, campaignIdToTest, null, null, null);

        assertThat(surveyUnitFetchedByStatesRepositoryPort.getCapturedStateTypes())
                .containsExactlyInAnyOrder(StateType.CLO, StateType.FIN);
    }

    @Test
    @DisplayName("Forwards campaign id to the repository")
    void shouldForwardCampaignId_toRepository() {
        surveyUnitFetchedByStatesRepositoryPort.willReturn(new PageImpl<>(List.of()));

        service.getSurveyUnitsByStatesAndCampaignId(null, List.of(StateType.FIN), campaignIdToTest, null, null, null);

        assertThat(surveyUnitFetchedByStatesRepositoryPort.getCapturedCampaignId()).isEqualTo(campaignIdToTest);
    }

    @Test
    @DisplayName("Returns empty list when campaign exists but has no completed survey units")
    void shouldReturnEmptyList_whenNoSurveyUnitsFound() {
        surveyUnitFetchedByStatesRepositoryPort.willReturn(new PageImpl<>(List.of()));

        Page<SurveyUnitFetchedByStatesAndCampaignIdView> result = service.getSurveyUnitsByStatesAndCampaignId(null, List.of(StateType.CLO), campaignIdToTest, null, null, null);

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
        return new SurveyUnitFetchedByStatesAndCampaignIdView(
            "su-1",
           "Display " + "su-1",
            "John",
            "Doe",
            "2024-01-01",
            "INA",
            "NPI",
            false,
            "A comment");
        }
}