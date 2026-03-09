package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;

import java.util.List;

public record SurveyUnitForInterviewer(
        SurveyUnitDB surveyUnit,
        List<CommunicationTemplate> communicationTemplates
) {
}
