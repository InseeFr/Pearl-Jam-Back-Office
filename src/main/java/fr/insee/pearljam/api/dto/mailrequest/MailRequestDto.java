package fr.insee.pearljam.api.dto.mailrequest;

import fr.insee.pearljam.api.domain.MailRequest;
import fr.insee.pearljam.api.domain.MailRequestStatus;
import fr.insee.pearljam.api.domain.MailRequestType;

public class MailRequestDto {

    private Long id;
    private String messhugahId;
    private Long date;
    private MailRequestType type;
    private MailRequestStatus status;

    public MailRequestDto() {
        super();
    }

    public MailRequestDto(MailRequest mr) {
        this.id = mr.getId();
        this.messhugahId = mr.getMesshugahId();
        this.date=mr.getDate();
        this.type =mr.getType();
        this.status =mr.getStatus();
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public MailRequestType getType() {
        return type;
    }

    public void setType(MailRequestType type) {
        this.type = type;
    }

    public MailRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MailRequestStatus status) {
        this.status = status;
    }

}
