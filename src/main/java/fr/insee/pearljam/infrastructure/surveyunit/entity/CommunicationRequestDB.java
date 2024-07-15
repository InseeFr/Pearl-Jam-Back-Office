package fr.insee.pearljam.infrastructure.surveyunit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.communication.*;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "communication_request")
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationRequestDB implements Serializable {

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

    @OneToMany(fetch = FetchType.LAZY, targetEntity = CommunicationRequestStatusDB.class, cascade = CascadeType.ALL, mappedBy = "communicationRequest", orphanRemoval = true)
    private List<CommunicationRequestStatusDB> status;

    /**
     * Create entity object from model object
     * @param surveyUnit survey unit entity
     * @param request model object
     * @return entity object
     */
    public static CommunicationRequestDB fromModel(CommunicationRequest request, SurveyUnit surveyUnit) {

        List<CommunicationRequestStatusDB> status = new ArrayList<>();
        CommunicationRequestDB communicationRequestDB = new CommunicationRequestDB(request.id(), request.messhugahId(), request.type(),
                request.reason(), request.medium(), request.emiter(), surveyUnit, status);

        if(request.status() != null) {
            status.addAll(request.status().stream()
                    .map(requestStatus -> CommunicationRequestStatusDB.fromModel(requestStatus, communicationRequestDB))
                    .toList());
        }

        return communicationRequestDB;
    }

    /**
     * Create model object from entity
     * @param request entity object
     * @return CommunicationRequest model object
     */
    public static CommunicationRequest toModel(CommunicationRequestDB request) {
        List<CommunicationRequestStatus> status = new ArrayList<>();
        if(request.getStatus() != null) {
            status = request.getStatus().stream()
                    .map(CommunicationRequestStatusDB::toModel).toList();
        }

        return new CommunicationRequest(request.getId(), request.getMesshugahId(), request.getType(),
                request.getReason(), request.getMedium(), request.getEmiter(), status);
    }
}
