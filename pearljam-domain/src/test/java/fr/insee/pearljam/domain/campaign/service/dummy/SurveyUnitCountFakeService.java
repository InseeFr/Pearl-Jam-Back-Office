package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.campaign.model.SurveyUnitCounts;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitCountService;

import java.util.List;

public class SurveyUnitCountFakeService implements SurveyUnitCountService {

    @Override
    public SurveyUnitCounts getSurveyUnitCounts(String campaignId, List<String> organizationUnitIds) {
        // Return default values for testing
        return new SurveyUnitCounts(0, 0, 0);
    }
}
