package fr.insee.pearljam.domain.surveyunit.readmodel;

import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;

public record SurveyUnitCommunication(
        Long date,
        CommunicationType type,
        CommunicationRequestReason reason

) {
}
