package fr.insee.pearljam.domain.reporting.projection;

import fr.insee.pearljam.domain.campaign.model.ContactAttemptConfiguration;
import fr.insee.pearljam.domain.campaign.model.ContactOutcomeConfiguration;
import fr.insee.pearljam.domain.campaign.model.IdentificationConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CampaignProjection {
	private String id;
	private String label;
	private String email;
	private Long managementStartDate;
	private Long interviewerStartDate;
	private Long identificationPhaseStartDate;
	private Long collectionStartDate;
	private Long collectionEndDate;
	private Long endDate;
	private Long allocated;
	private Long toProcessInterviewer;
	private Long toAffect;
	private Long toFollowUp;
	private Long toReview;
	private Long finalized;
	private IdentificationConfiguration identificationConfiguration;
	private ContactAttemptConfiguration contactAttemptConfiguration;
	private ContactOutcomeConfiguration contactOutcomeConfiguration;
	private boolean collectNextContacts;
}
