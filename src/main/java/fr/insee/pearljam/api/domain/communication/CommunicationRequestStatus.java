package fr.insee.pearljam.api.domain.communication;

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

import fr.insee.pearljam.api.dto.communication.CommunicationRequestStatusDto;

@Entity
@Table
public class CommunicationRequestStatus implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long date;

    @Enumerated(EnumType.STRING)
    @Column
    private CommunicationStatusType status;

    @ManyToOne(fetch = FetchType.LAZY)
    private CommunicationRequest communicationRequest;

    public CommunicationRequestStatus() {
    }

    public CommunicationRequestStatus(CommunicationRequestStatusDto crsDto) {
        this.date = crsDto.getDate();
        this.status = crsDto.getStatus();
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
