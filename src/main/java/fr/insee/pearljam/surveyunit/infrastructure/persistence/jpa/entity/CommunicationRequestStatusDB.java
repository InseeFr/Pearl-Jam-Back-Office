package fr.insee.pearljam.surveyunit.infrastructure.persistence.jpa.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import java.io.Serializable;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationRequestStatus;
import fr.insee.pearljam.surveyunit.domain.model.communication.CommunicationStatusType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "communication_request_status")
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommunicationRequestStatusDB implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long date;

    @Enumerated(EnumType.STRING)
    @Column
    private CommunicationStatusType status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Ensures cascading behavior
    private CommunicationRequestDB communicationRequest;

    public static CommunicationRequestStatusDB fromModel(CommunicationRequestStatus requestStatus, CommunicationRequestDB communicationRequest) {
        return new CommunicationRequestStatusDB(
            requestStatus.id(),
            requestStatus.date(),
            requestStatus.status(),
            communicationRequest
        );
    }

    public static CommunicationRequestStatus toModel(CommunicationRequestStatusDB requestStatus) {
        return new CommunicationRequestStatus(
            requestStatus.getId(),
            requestStatus.getDate(),
            requestStatus.getStatus()
        );
    }
}
