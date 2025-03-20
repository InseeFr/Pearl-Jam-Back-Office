package fr.insee.pearljam.infrastructure.surveyunit.entity;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequest;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestEmitter;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestReason;
import fr.insee.pearljam.domain.surveyunit.model.communication.CommunicationRequestStatus;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDBId;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

    @Embedded
    private CommunicationTemplateDBId communicationTemplateDBId;

    @Enumerated(EnumType.STRING)
    @Column
    private CommunicationRequestReason reason;

    @Enumerated(EnumType.STRING)
    @Column
    private CommunicationRequestEmitter emitter;

    @ManyToOne(fetch = FetchType.LAZY)
    private SurveyUnit surveyUnit;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = CommunicationRequestStatusDB.class, cascade = CascadeType.ALL, mappedBy = "communicationRequest", orphanRemoval = true)
    private List<CommunicationRequestStatusDB> status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", referencedColumnName = "campaign_id", insertable = false, updatable = false)
    @JoinColumn(name = "meshuggah_id", referencedColumnName = "meshuggah_id", insertable = false, updatable = false)
    private CommunicationTemplateDB communicationTemplate;

    public CommunicationRequestDB(Long id, CommunicationTemplateDBId communicationTemplateDBId,
        CommunicationRequestReason reason, CommunicationRequestEmitter emitter,
        SurveyUnit surveyUnit, List<CommunicationRequestStatusDB> status) {
        this.id = id;
        this.communicationTemplateDBId = communicationTemplateDBId;
        this.reason = reason;
        this.emitter = emitter;
        this.surveyUnit = surveyUnit;
        this.status = status;
    }

    /**
     * Create entity object from model object
     * @param surveyUnit survey unit entity
     * @param request model object
     * @return entity object
     */
    public static CommunicationRequestDB fromModel(CommunicationRequest request, SurveyUnit surveyUnit, CommunicationTemplateDB communicationTemplateDB) {

        List<CommunicationRequestStatusDB> status = new ArrayList<>();
        CommunicationRequestDB communicationRequestDB = new CommunicationRequestDB(request.id(), communicationTemplateDB.getCommunicationTemplateDBId(),
                request.reason(), request.emitter(), surveyUnit, status);

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

        return new CommunicationRequest(request.getId(), request.communicationTemplateDBId.getCampaignId(), request.communicationTemplateDBId.getMeshuggahId(),
                request.getReason(), request.getEmitter(), status);
    }

    /**
     * Converts a set of CommunicationRequestDB entities to a set of CommunicationRequest models.
     * @param requests set of CommunicationRequestDB entities
     * @return set of CommunicationRequest models
     */
    public static Set<CommunicationRequest> toModel(Set<CommunicationRequestDB> requests) {
        return requests.stream()
                .map(CommunicationRequestDB::toModel)
                .collect(Collectors.toSet());
    }
}
