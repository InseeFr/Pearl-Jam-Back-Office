package fr.insee.pearljam.infrastructure.campaign.entity;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationMedium;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationTemplate;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "communication_template",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"type", "medium", "campaign_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunicationTemplateDB implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String meshuggahId;

    @Column
    @Enumerated(EnumType.STRING)
    private CommunicationMedium medium;

    @Column
    @Enumerated(EnumType.STRING)
    private CommunicationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", insertable = false, updatable = false)
    private Campaign campaign;

    public static List<CommunicationTemplate> toModel(List<CommunicationTemplateDB> communicationTemplatesDB) {
        return communicationTemplatesDB.stream()
                .map(CommunicationTemplateDB::toModel)
                .toList();
    }

    public static CommunicationTemplate toModel(CommunicationTemplateDB communicationTemplate) {
        if(communicationTemplate == null) {
            return null;
        }
        return new CommunicationTemplate(
                        communicationTemplate.getId(),
                        communicationTemplate.getMeshuggahId(),
                        communicationTemplate.getMedium(),
                        communicationTemplate.getType());
    }

    public static List<CommunicationTemplateDB> fromModel(List<CommunicationTemplate> communicationTemplates, Campaign campaign) {
        return communicationTemplates.stream()
                .map(communicationTemplate -> new CommunicationTemplateDB(null,
                        communicationTemplate.meshuggahId(),
                        communicationTemplate.medium(),
                        communicationTemplate.type(),
                        campaign))
                .toList();
    }
}
