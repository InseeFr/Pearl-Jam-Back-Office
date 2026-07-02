package fr.insee.pearljam.domain.surveyunit.service.application;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.domain.surveyunit.stub.SurveyUnitFetchPortStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitCompletedServiceTest {

    private SurveyUnitFetchPortStub surveyUnitFetchPortStub;
    private SurveyUnitCompletedService service;

    @BeforeEach
    void setUp() {
        DateService dateService = new FixedDateService();
        surveyUnitFetchPortStub = new SurveyUnitFetchPortStub();
        service = new SurveyUnitCompletedService(surveyUnitFetchPortStub, dateService);
    }

    @Test
    @DisplayName("Queries fetch port with CLO and FIN states")
    void shouldQueryWithCloAndFinStates() {
        surveyUnitFetchPortStub.willReturn(new PageImpl<>(List.of()));

        service.getCompletedSurveyUnits(
                null,
                "campaign-01",
                null,
                PageRequest.of(0, 10),
                surveyUnits -> surveyUnits);

        assertThat(surveyUnitFetchPortStub.getCapturedStateTypes())
                .containsExactlyInAnyOrder(StateType.CLO, StateType.FIN);
    }

    @Test
    @DisplayName("Forwards campaign id to the fetch port")
    void shouldForwardCampaignId_toFetchPort() {
        surveyUnitFetchPortStub.willReturn(new PageImpl<>(List.of()));

        service.getCompletedSurveyUnits(
                null,
                "campaign-01",
                null,
                PageRequest.of(0, 10),
                surveyUnits -> surveyUnits);

        assertThat(surveyUnitFetchPortStub.getCapturedCampaignId()).isEqualTo("campaign-01");
    }

    @Test
    @DisplayName("Returns result transformed by presenter")
    void shouldReturnPresentedResult() {
        SurveyUnitFetchedByStatesAndCampaignIdView su1 = surveyUnitView();
        SurveyUnitFetchedByStatesAndCampaignIdView su2 = surveyUnitView();

        surveyUnitFetchPortStub.willReturn(new PageImpl<>(List.of(su1, su2)));

        Page<SurveyUnitFetchedByStatesAndCampaignIdView> result =
                service.getCompletedSurveyUnits(
                        null,
                        "campaign-01",
                        null,
                        PageRequest.of(0, 10),
                        surveyUnits -> surveyUnits);

        assertThat(result).containsExactly(su1, su2);
    }

    @Test
    @DisplayName("Returns empty list when no completed survey units found")
    void shouldReturnEmptyList_whenNoSurveyUnitsFound() {
        surveyUnitFetchPortStub.willReturn(new PageImpl<>(List.of()));

        Page<SurveyUnitFetchedByStatesAndCampaignIdView> result =
                service.getCompletedSurveyUnits(
                        null,
                        "campaign-empty",
                        null,
                        PageRequest.of(0, 10),
                        surveyUnits -> surveyUnits);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Delegates result to presenter and returns its output")
    void shouldDelegateToPresenter() {
        surveyUnitFetchPortStub.willReturn(
                new PageImpl<>(List.of(surveyUnitView(), surveyUnitView()))
        );

        Long result = service.getCompletedSurveyUnits(
                null,
                "campaign-01",
                null,
                PageRequest.of(0, 10),
                Page::getTotalElements);

        assertThat(result).isEqualTo(2);
    }

    private SurveyUnitFetchedByStatesAndCampaignIdView surveyUnitView() {
        return new SurveyUnitFetchedByStatesAndCampaignIdView(
                "su-1",
                "Display su-1",
                "John",
                "Doe",
                "1704067200000", // 2024-01-01
                "INA",
                "NPI",
                false,
                "A comment");
    }
}