package fr.insee.pearljam.api.dto.communication;

import java.io.Serializable;

import fr.insee.pearljam.api.domain.communication.CommunicationRequestStatus;
import fr.insee.pearljam.api.domain.communication.CommunicationStatusType;

public class CommunicationRequestStatusDto implements Serializable {

    private Long id;
    private Long date;
    private CommunicationStatusType status;

    public CommunicationRequestStatusDto() {

    }

    public CommunicationRequestStatusDto(CommunicationRequestStatus crs) {
        this.id = crs.getId();
        this.date = crs.getDate();
        this.status = crs.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public CommunicationStatusType getStatus() {
        return status;
    }

    public void setStatus(CommunicationStatusType status) {
        this.status = status;
    }

}
