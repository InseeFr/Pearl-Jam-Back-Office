package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestStatus;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationStatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

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
