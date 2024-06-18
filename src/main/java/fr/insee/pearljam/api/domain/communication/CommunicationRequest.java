package fr.insee.pearljam.api.domain.communication;

import java.io.Serializable;
import java.util.List;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.communication.CommunicationRequestDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
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

    public CommunicationRequest(CommunicationRequestDto dto, SurveyUnit surveyUnit) {
        this.messhugahId = dto.getMesshugahId();
        this.type = dto.getType();
        this.medium = dto.getMedium();
        this.emiter = dto.getEmiter();
        this.reason = dto.getReason();
        this.status = dto.getStatus().stream().map(statusDto -> new CommunicationRequestStatus(statusDto, this))
                .toList();
        this.surveyUnit = surveyUnit;
    }

}
