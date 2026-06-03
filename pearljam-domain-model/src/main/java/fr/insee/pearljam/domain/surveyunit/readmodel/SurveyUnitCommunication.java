package fr.insee.pearljam.domain.surveyunit.readmodel;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;

public record SurveyUnitCommunication(
        Long date,
        CommunicationType type
) {
}
