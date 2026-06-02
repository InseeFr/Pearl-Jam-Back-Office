package fr.insee.pearljam.domain.surveyunit.stub;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitFetchedByStatesRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class SurveyUnitFetchedByStatesRepositoryStub implements SurveyUnitFetchedByStatesRepositoryPort {

    private Page<SurveyUnitFetchedByStatesAndCampaignIdView> stubbedResults = new PageImpl<SurveyUnitFetchedByStatesAndCampaignIdView>(List.of());
    @Getter
    private List<StateType> capturedStateTypes;
    @Getter
    private String capturedCampaignId;

    public void willReturn(Page<SurveyUnitFetchedByStatesAndCampaignIdView> results) {
        this.stubbedResults = results;
    }
    @Override
    public Page<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(List<StateType> stateTypes,
                                                                                                String campaignId,
                                                                                                String search,
                                                                                                Pageable pageable) {
        this.capturedStateTypes = stateTypes;
        this.capturedCampaignId = campaignId;
        return stubbedResults;
    }
}
