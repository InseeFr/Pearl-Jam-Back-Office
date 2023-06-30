package fr.insee.pearljam.api.dto.communication;

import java.util.List;
import java.util.stream.Collectors;

import fr.insee.pearljam.api.domain.communication.CommunicationRequestEmiter;
import fr.insee.pearljam.api.domain.communication.CommunicationRequestMedium;
import fr.insee.pearljam.api.domain.communication.CommunicationRequest;
import fr.insee.pearljam.api.domain.communication.CommunicationRequestReason;
import fr.insee.pearljam.api.domain.communication.CommunicationRequestType;

public class CommunicationRequestDto {

    private Long id;
    private String messhugahId;
    private CommunicationRequestType type;
    private CommunicationRequestReason reason;
    private CommunicationRequestMedium medium;
    private CommunicationRequestEmiter emiter;
    private List<CommunicationRequestStatusDto> status;

    public CommunicationRequestDto() {
        super();
    }

    public CommunicationRequestDto(CommunicationRequest commRequest) {
        this.id = commRequest.getId();
        this.messhugahId = commRequest.getMesshugahId();
        this.type = commRequest.getType();
        this.reason = commRequest.getReason();
        this.medium = commRequest.getMedium();
        this.emiter = commRequest.getEmiter();
        this.status = commRequest.getStatus().stream()
                .map(CommunicationRequestStatusDto::new).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMesshugahId() {
        return messhugahId;
    }

    public void setMesshugahId(String messhugahId) {
        this.messhugahId = messhugahId;
    }

    public CommunicationRequestType getType() {
        return type;
    }

    public void setType(CommunicationRequestType type) {
        this.type = type;
    }

    public CommunicationRequestReason getReason() {
        return reason;
    }

    public void setReason(CommunicationRequestReason reason) {
        this.reason = reason;
    }

    public CommunicationRequestMedium getMedium() {
        return medium;
    }

    public void setMedium(CommunicationRequestMedium medium) {
        this.medium = medium;
    }

    public List<CommunicationRequestStatusDto> getStatus() {
        return status;
    }

    public void setStatus(List<CommunicationRequestStatusDto> status) {
        this.status = status;
    }

    public CommunicationRequestEmiter getEmiter() {
        return emiter;
    }

    public void setEmiter(CommunicationRequestEmiter emiter) {
        this.emiter = emiter;
    }

}
