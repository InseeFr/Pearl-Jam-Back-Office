package fr.insee.pearljam.surveyunit.domain.model;

import fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity.SurveyUnit;
import fr.insee.pearljam.campaign.domain.model.communication.CommunicationTemplate;

import java.util.List;

public record SurveyUnitForInterviewer(
        SurveyUnit surveyUnit,
        List<CommunicationTemplate> communicationTemplates
) {
}
