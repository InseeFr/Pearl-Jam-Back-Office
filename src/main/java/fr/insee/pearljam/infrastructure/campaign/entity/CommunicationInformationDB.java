package fr.insee.pearljam.infrastructure.campaign.entity;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.domain.campaign.model.communication.CommunicationInformation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "communication_information")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class CommunicationInformationDB implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private CommunicationInformationDBId communicationInformationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_unit_id", insertable = false, updatable = false)
    private OrganizationUnit organizationUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", insertable = false, updatable = false)
    private Campaign campaign;

    private String mail;
    private String address;
    private String tel;

    public static CommunicationInformationDB fromModel(CommunicationInformation communicationInformation, Campaign campaign, OrganizationUnit organizationUnit) {
        CommunicationInformationDBId id = new CommunicationInformationDBId(organizationUnit.getId(), campaign.getId());
        return new CommunicationInformationDB(id,
                organizationUnit, campaign,
                communicationInformation.mail(),
                communicationInformation.address(),
                communicationInformation.tel());
    }

    public static CommunicationInformation toModel(CommunicationInformationDB communicationInformationDB) {
        return new CommunicationInformation(
                communicationInformationDB.getCommunicationInformationId().getCampaignId(),
                communicationInformationDB.getCommunicationInformationId().getOrganizationUnitId(),
                communicationInformationDB.getAddress(),
                communicationInformationDB.getMail(),
                communicationInformationDB.getTel());
    }

    public static List<CommunicationInformation> toModel(List<CommunicationInformationDB> communicationInformationDBS) {
        return communicationInformationDBS.stream()
                .map(CommunicationInformationDB::toModel)
                .toList();
    }
}
