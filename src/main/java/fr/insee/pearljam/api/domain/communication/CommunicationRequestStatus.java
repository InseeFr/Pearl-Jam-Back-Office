package fr.insee.pearljam.api.domain.communication;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

    public CommunicationRequestStatus(CommunicationRequestStatusDto crsDto, CommunicationRequest communicationRequest) {
        this.date = crsDto.getDate();
        this.status = crsDto.getStatus();
        this.communicationRequest = communicationRequest;
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
