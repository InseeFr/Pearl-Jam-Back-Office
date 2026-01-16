package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;

import java.util.List;

public record SurveyUnitForInterviewer(
        SurveyUnit surveyUnit,
        List<CommunicationTemplate> communicationTemplates
) {
}
