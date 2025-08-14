package fr.insee.pearljam.api.dto.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDto {
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
	private Boolean preference;
	private IdentificationConfiguration identificationConfiguration;
	private ContactAttemptConfiguration contactAttemptConfiguration;
	private ContactOutcomeConfiguration contactOutcomeConfiguration;
	private List<ReferentDto> referents;

	public CampaignDto(String id, String label, Long managementStartDate, Long endDate) {
		super();
		this.id = id;
		this.label = label;
		this.managementStartDate = managementStartDate;
		this.endDate = endDate;
	}

	// keep it for creation in SQL
	public CampaignDto(String id, String label, String email, IdentificationConfiguration identConfig,
			ContactOutcomeConfiguration contOutConfig, ContactAttemptConfiguration contAttConfig) {
		super();
		this.id = id;
		this.label = label;
		this.email = email;
		this.identificationConfiguration = identConfig;
		this.contactOutcomeConfiguration = contOutConfig;
		this.contactAttemptConfiguration = contAttConfig;
	}

	public void setCampaignStats(List<Object[]> obj) {
		if (obj != null && !obj.isEmpty() && obj.getFirst().length > 3 && obj.getFirst()[0] != null) {
			this.toProcessInterviewer = (Long) obj.getFirst()[0];
			this.toReview = (Long) (obj.getFirst()[1]);
			this.finalized = (Long) (obj.getFirst()[2]);
			this.allocated = (Long) (obj.getFirst()[3]);
			this.toAffect = 0L;
			this.toFollowUp = 0L;
		}
	}
}
