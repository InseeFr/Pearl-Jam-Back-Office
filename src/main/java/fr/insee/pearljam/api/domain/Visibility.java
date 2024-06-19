package fr.insee.pearljam.api.domain;

import java.io.Serializable;

import fr.insee.pearljam.api.dto.visibility.VisibilityDto;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity Visibility : represent the entity table in DB
 * 
 * @author Corcaud Samuel
 * 
 */
@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
public class Visibility implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private VisibilityId visibilityId;
	/**
	 * The organizationUnit associated to Visibility
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	// @MapsId("organization_unit_id")
	@JoinColumn(name = "organization_unit_id", referencedColumnName = "id", insertable = false, updatable = false)
	private OrganizationUnit organizationUnit;

	/**
	 * The Campaign associated to Visibility
	 */

	@ManyToOne(fetch = FetchType.LAZY)
	// @MapsId("campaign_id")
	@JoinColumn(name = "campaign_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Campaign campaign;

	/**
	 * The start date of Visibility of management
	 */
	private Long managementStartDate;

	/**
	 * The start date of Visibility of interviewer
	 */
	private Long interviewerStartDate;

	/**
	 * The start date of identification phase
	 */
	private Long identificationPhaseStartDate;

	/**
	 * The start date of collection
	 */
	private Long collectionStartDate;

	/**
	 * The end date of collection
	 */
	private Long collectionEndDate;

	/**
	 * The end date
	 */
	private Long endDate;

	public Visibility(VisibilityDto vis) {
		super();
		this.collectionEndDate = vis.getCollectionEndDate();
		this.collectionStartDate = vis.getCollectionStartDate();
		this.endDate = vis.getEndDate();
		this.identificationPhaseStartDate = vis.getIdentificationPhaseStartDate();
		this.interviewerStartDate = vis.getInterviewerStartDate();
		this.managementStartDate = vis.getManagementStartDate();
	}

	public Visibility(Long managerStartDate, Long interviewerStartDate, Long identificationPhaseStartDate,
			Long collectionstartDate, Long collectionEndDate, Long endDate, Campaign campaign, OrganizationUnit ou) {
		this.collectionStartDate = collectionstartDate;
		this.collectionEndDate = collectionEndDate;
		this.identificationPhaseStartDate = identificationPhaseStartDate;
		this.interviewerStartDate = interviewerStartDate;
		this.managementStartDate = managerStartDate;
		this.endDate = endDate;
		this.campaign = campaign;
		this.organizationUnit = ou;
		this.visibilityId = new VisibilityId(ou.getId(), campaign.getId());
	}
}
