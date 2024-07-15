package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestStatus;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType;

public record CommunicationRequestStatusDto(
    Long id,
    Long date,
    CommunicationStatusType status) {

    public static CommunicationRequestStatusDto fromModel(CommunicationRequestStatus requestStatus) {
        return new CommunicationRequestStatusDto(requestStatus.id(), requestStatus.date(), requestStatus.status());
    }

    public static CommunicationRequestStatus toModel(CommunicationRequestStatusDto requestStatus) {
        return new CommunicationRequestStatus(requestStatus.id(), requestStatus.date(), requestStatus.status());
    }
}
