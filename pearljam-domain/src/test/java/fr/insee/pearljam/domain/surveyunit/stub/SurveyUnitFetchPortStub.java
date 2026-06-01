package fr.insee.pearljam.domain.surveyunit.stub;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitFetchPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class SurveyUnitFetchPortStub implements SurveyUnitFetchPort {

    private List<SurveyUnitFetchedByStatesAndCampaignIdView> stubbedResults = new ArrayList<>();
    @Getter
    private String capturedCampaignId;
    @Getter
    private List<StateType> capturedStateTypes;

    public void willReturn(List<SurveyUnitFetchedByStatesAndCampaignIdView> results) {
        this.stubbedResults = results;
    }

    @Override
    public List<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(List<StateType> stateTypes, String campaignId) {
        this.capturedStateTypes = stateTypes;
        this.capturedCampaignId = campaignId;
        return stubbedResults;
    }
}
