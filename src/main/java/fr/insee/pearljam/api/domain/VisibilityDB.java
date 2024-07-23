package fr.insee.pearljam.api.domain;

import java.io.Serializable;
import java.util.List;

import fr.insee.pearljam.domain.campaign.model.Visibility;
import fr.insee.pearljam.infrastructure.campaign.entity.CommunicationTemplateDB;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity(name = "visibility")
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class VisibilityDB implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private VisibilityDBId visibilityId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_unit_id", insertable = false, updatable = false)
	private OrganizationUnit organizationUnit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "campaign_id", insertable = false, updatable = false)
	private Campaign campaign;


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "visibility", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CommunicationTemplateDB> communicationTemplates;

	private Long managementStartDate;
	private Long interviewerStartDate;
	private Long identificationPhaseStartDate;
	private Long collectionStartDate;
	private Long collectionEndDate;
	private Long endDate;

	public void updateDates(Visibility visibilityToUpdate) {
		String ouId = visibilityToUpdate.organizationalUnitId();
		String campaignId = visibilityToUpdate.campaignId();
		if (visibilityToUpdate.managementStartDate() != null) {
			log.info("Updating management start date for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setManagementStartDate(visibilityToUpdate.managementStartDate());
		}
		if (visibilityToUpdate.interviewerStartDate() != null) {
			log.info("Updating interviewer start date for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setInterviewerStartDate(visibilityToUpdate.interviewerStartDate());
		}
		if (visibilityToUpdate.identificationPhaseStartDate() != null) {
			log.info("Updating identification phase start date for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setIdentificationPhaseStartDate(visibilityToUpdate.identificationPhaseStartDate());
		}
		if (visibilityToUpdate.collectionStartDate() != null) {
			log.info("Updating collection start date for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setCollectionStartDate(visibilityToUpdate.collectionStartDate());
		}
		if (visibilityToUpdate.collectionEndDate() != null) {
			log.info("Updating collection end date for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setCollectionEndDate(visibilityToUpdate.collectionEndDate());
		}
		if (visibilityToUpdate.endDate() != null) {
			log.info("Updating end date for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setEndDate(visibilityToUpdate.endDate());
		}
	}

	public static VisibilityDB fromModel(Visibility visibility, Campaign campaign, OrganizationUnit organizationUnit) {
		VisibilityDBId id = new VisibilityDBId(organizationUnit.getId(), campaign.getId());
		return new VisibilityDB(id,
				organizationUnit, campaign,
				CommunicationTemplateDB.fromModel(visibility.communicationTemplates()),
				visibility.managementStartDate(), visibility.interviewerStartDate(), visibility.identificationPhaseStartDate(),
				visibility.collectionStartDate(), visibility.collectionEndDate(), visibility.endDate());
	}

	public static Visibility toModel(VisibilityDB visibilityDB) {
		return new Visibility(
				visibilityDB.getVisibilityId().getCampaignId(),
				visibilityDB.getVisibilityId().getOrganizationUnitId(),
				visibilityDB.getManagementStartDate(),
				visibilityDB.getInterviewerStartDate(),
				visibilityDB.getIdentificationPhaseStartDate(),
				visibilityDB.getCollectionStartDate(),
				visibilityDB.getCollectionEndDate(),
				visibilityDB.getEndDate(),
				CommunicationTemplateDB.toModel(visibilityDB.getCommunicationTemplates()));
	}

	public static List<Visibility> toModel(List<VisibilityDB> visibilityDBs) {
		return visibilityDBs.stream()
				.map(VisibilityDB::toModel)
				.toList();
	}
}
