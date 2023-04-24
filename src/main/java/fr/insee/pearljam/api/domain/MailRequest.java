package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fr.insee.pearljam.api.dto.mailrequest.MailRequestDto;

@Entity
@Table
public class MailRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String messhugahId;

    @Column
    private Long date;

    @Enumerated(EnumType.STRING)
    @Column
    private MailRequestType type;

    @Enumerated(EnumType.STRING)
    @Column
    private MailRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private SurveyUnit surveyUnit;

    public MailRequest() {
    }

    public MailRequest(MailRequestDto dto, SurveyUnit surveyUnit) {
        this.messhugahId = dto.getMesshugahId();
        this.date = dto.getDate();
        this.type = dto.getType();
        this.status = dto.getStatus();
        this.id = dto.getId();
        this.surveyUnit = surveyUnit;
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

    public SurveyUnit getSurveyUnit() {
        return surveyUnit;
    }

    public void setSurveyUnit(SurveyUnit surveyUnit) {
        this.surveyUnit = surveyUnit;
    }

}
