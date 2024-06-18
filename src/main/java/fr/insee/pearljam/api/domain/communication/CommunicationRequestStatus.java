package fr.insee.pearljam.api.domain.communication;

import java.io.Serializable;

import fr.insee.pearljam.api.dto.communication.CommunicationRequestStatusDto;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
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

    public CommunicationRequestStatus(CommunicationRequestStatusDto crsDto, CommunicationRequest communicationRequest) {
        this.date = crsDto.getDate();
        this.status = crsDto.getStatus();
        this.communicationRequest = communicationRequest;
    }

}
