package fr.insee.pearljam.api.surveyunit.dto;

import java.util.ArrayList;
import java.util.List;

import fr.insee.pearljam.domain.surveyunit.model.communication.*;

public record CommunicationRequestDto(
    Long id,
    String messhugahId,
    CommunicationRequestType type,
    CommunicationRequestReason reason,
    CommunicationRequestMedium medium,
    CommunicationRequestEmiter emiter,
    List<CommunicationRequestStatusDto> status) {

    public static CommunicationRequest toModel(CommunicationRequestDto request) {
        List<CommunicationRequestStatus> status = new ArrayList<>();
        if(request.status() != null) {
            status = request.status().stream()
                    .map(CommunicationRequestStatusDto::toModel).toList();
        }

        return new CommunicationRequest(request.id(), request.messhugahId(), request.type(),
                request.reason(), request.medium(), request.emiter(), status);
    }

    public static CommunicationRequestDto fromModel(CommunicationRequest request) {
        List<CommunicationRequestStatusDto> status = request.status().stream()
                .map(CommunicationRequestStatusDto::fromModel).toList();
        return new CommunicationRequestDto(request.id(), request.messhugahId(), request.type(),
                request.reason(), request.medium(), request.emiter(), status);
    }
}
