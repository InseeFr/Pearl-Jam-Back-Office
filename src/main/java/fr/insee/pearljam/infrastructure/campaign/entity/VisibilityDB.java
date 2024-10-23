package fr.insee.pearljam.infrastructure.campaign.entity;

import java.io.Serializable;
import java.util.List;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.OrganizationUnit;
import fr.insee.pearljam.domain.campaign.model.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "visibility")
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

	private Long managementStartDate;
	private Long interviewerStartDate;
	private Long identificationPhaseStartDate;
	private Long collectionStartDate;
	private Long collectionEndDate;
	private Long endDate;
	private boolean useLetterCommunication;
	private String mail;
	private String tel;

	public void update(Visibility visibilityToUpdate) {
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

		if (visibilityToUpdate.useLetterCommunication() != null) {
			log.info("Updating letter communication usage for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setUseLetterCommunication(visibilityToUpdate.useLetterCommunication());
		}

		if (visibilityToUpdate.mail() != null) {
			log.info("Updating mail communication usage for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setMail(visibilityToUpdate.mail());
		}

		if (visibilityToUpdate.tel() != null) {
			log.info("Updating tel communication usage for campaign {} and Organizational Unit {}", campaignId, ouId);
			this.setTel(visibilityToUpdate.tel());
		}
	}

	public static VisibilityDB fromModel(Visibility visibility, Campaign campaign, OrganizationUnit organizationUnit) {
		VisibilityDBId id = new VisibilityDBId(organizationUnit.getId(), campaign.getId());
		return new VisibilityDB(id,
				organizationUnit, campaign,
				visibility.managementStartDate(), visibility.interviewerStartDate(), visibility.identificationPhaseStartDate(),
				visibility.collectionStartDate(), visibility.collectionEndDate(), visibility.endDate(), visibility.useLetterCommunication(),
				visibility.mail(), visibility.tel());
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
				visibilityDB.isUseLetterCommunication(),
				visibilityDB.getMail(),
				visibilityDB.getTel());
	}

	public static List<Visibility> toModel(List<VisibilityDB> visibilityDBs) {
		return visibilityDBs.stream()
				.map(VisibilityDB::toModel)
				.toList();
	}
}
