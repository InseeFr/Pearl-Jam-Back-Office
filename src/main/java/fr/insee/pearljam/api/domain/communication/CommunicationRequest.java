package fr.insee.pearljam.api.domain.communication;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestDto;

@Entity
@Table
public class CommunicationRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String messhugahId;

    @Enumerated(EnumType.STRING)
    @Column
    private CommunicationRequestType type;

    @Enumerated(EnumType.STRING)
    @Column
    private CommunicationRequestReason reason;

    @Enumerated(EnumType.STRING)
    @Column
    private CommunicationRequestMedium medium;

    @Enumerated(EnumType.STRING)
    @Column
    private CommunicationRequestEmiter emiter;

    @ManyToOne(fetch = FetchType.LAZY)
    private SurveyUnit surveyUnit;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = CommunicationRequestStatus.class, cascade = CascadeType.ALL, mappedBy = "communicationRequest", orphanRemoval = true)
    private List<CommunicationRequestStatus> status;

    public CommunicationRequest() {
    }

    public CommunicationRequest(CommunicationRequestDto dto, SurveyUnit surveyUnit) {
        this.messhugahId = dto.getMesshugahId();
        this.type = dto.getType();
        this.medium = dto.getMedium();
        this.emiter = dto.getEmiter();
        this.reason = dto.getReason();
        this.status = dto.getStatus().stream().map(statusDto -> new CommunicationRequestStatus(statusDto,this)).collect(Collectors.toList());
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

    public SurveyUnit getSurveyUnit() {
        return surveyUnit;
    }

    public void setSurveyUnit(SurveyUnit surveyUnit) {
        this.surveyUnit = surveyUnit;
    }

    public CommunicationRequestType getType() {
        return type;
    }

    public void setType(CommunicationRequestType type) {
        this.type = type;
    }

    public List<CommunicationRequestStatus> getStatus() {
        return status;
    }

    public void setStatus(List<CommunicationRequestStatus> status) {
        this.status = status;
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

    public CommunicationRequestEmiter getEmiter() {
        return emiter;
    }

    public void setEmiter(CommunicationRequestEmiter emiter) {
        this.emiter = emiter;
    }

}
