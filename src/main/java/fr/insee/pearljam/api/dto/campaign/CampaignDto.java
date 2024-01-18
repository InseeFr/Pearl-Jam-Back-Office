package fr.insee.pearljam.api.dto.campaign;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	private Boolean communicationRequestConfiguration;
	private List<ReferentDto> referents;

	public CampaignDto(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}

	public CampaignDto(Campaign camp) {
		super();
		this.id = camp.getId();
		this.label = camp.getLabel();
	}

	public CampaignDto(String id, String label, Long managementStartDate, Long endDate) {
		super();
		this.id = id;
		this.label = label;
		this.managementStartDate = managementStartDate;
		this.endDate = endDate;
	}

	public CampaignDto(String id, String label, String email, IdentificationConfiguration identConfig,
			ContactOutcomeConfiguration contOutConfig, ContactAttemptConfiguration contAttConfig,
			Boolean communicationRequestConfiguration) {
		super();
		this.id = id;
		this.label = label;
		this.email = email;
		this.identificationConfiguration = identConfig;
		this.contactOutcomeConfiguration = contOutConfig;
		this.contactAttemptConfiguration = contAttConfig;
		this.communicationRequestConfiguration = Optional.ofNullable(communicationRequestConfiguration).orElse(false);
	}

	public void setCampaignStats(List<Object[]> obj) {
		if (obj != null && !obj.isEmpty() && obj.get(0).length > 3 && obj.get(0)[0] != null) {
			this.toProcessInterviewer = (Long) obj.get(0)[0];
			this.toReview = (Long) (obj.get(0)[1]);
			this.finalized = (Long) (obj.get(0)[2]);
			this.allocated = (Long) (obj.get(0)[3]);
			this.toAffect = 0L;
			this.toFollowUp = 0L;
		}
	}

}
