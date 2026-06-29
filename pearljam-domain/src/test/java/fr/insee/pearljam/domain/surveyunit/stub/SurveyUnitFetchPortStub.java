package fr.insee.pearljam.domain.surveyunit.stub;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitFetchPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

public class SurveyUnitFetchPortStub implements SurveyUnitFetchPort {

    private Page<SurveyUnitFetchedByStatesAndCampaignIdView> stubbedResults = new PageImpl<>(List.of());
    @Getter
    private List<StateType> capturedStateTypes;
    @Getter
    private String capturedCampaignId;

    public void willReturn(Page<SurveyUnitFetchedByStatesAndCampaignIdView> results) {
        this.stubbedResults = results;
    }

    @Override
    public Page<SurveyUnitFetchedByStatesAndCampaignIdView> getSurveyUnitsByStatesAndCampaignId(
                                                                                                String userId,
                                                                                                List<StateType> stateTypes,
                                                                                                String campaignId,
                                                                                                String search,
                                                                                                Instant endDateBefore,
                                                                                                Pageable pageable) {
        this.capturedStateTypes = stateTypes;
        this.capturedCampaignId = campaignId;
        return stubbedResults;
    }
}
