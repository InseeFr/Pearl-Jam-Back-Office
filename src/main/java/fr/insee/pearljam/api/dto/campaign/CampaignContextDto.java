package fr.insee.pearljam.api.dto.campaign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.domain.ContactAttemptConfiguration;
import fr.insee.pearljam.api.domain.ContactOutcomeConfiguration;
import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.api.dto.referent.ReferentDto;
import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignContextDto {
	private String campaign;
	private String campaignLabel;
	private List<VisibilityContextDto> visibilities;
	private List<ReferentDto> referents;
	private String email;
	private IdentificationConfiguration identificationConfiguration;
	private ContactOutcomeConfiguration contactOutcomeConfiguration;
	private ContactAttemptConfiguration contactAttemptConfiguration;

	public CampaignContextDto() {
		super();
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public String getCampaignLabel() {
		return campaignLabel;
	}

	public void setCampaignLabel(String campaignLabel) {
		this.campaignLabel = campaignLabel;
	}

	public List<VisibilityContextDto> getVisibilities() {
		return visibilities;
	}

	public void setVisibilities(List<VisibilityContextDto> visibilities) {
		this.visibilities = visibilities;
	}

	public IdentificationConfiguration getIdentificationConfiguration() {
		return this.identificationConfiguration;
	}

	public void setIdentificationConfiguration(IdentificationConfiguration identificationConfiguration) {
		this.identificationConfiguration = identificationConfiguration;
	}

	public ContactOutcomeConfiguration getContactOutcomeConfiguration() {
		return this.contactOutcomeConfiguration;
	}

	public void setContactOutcomeConfiguration(ContactOutcomeConfiguration contactOutcomeConfiguration) {
		this.contactOutcomeConfiguration = contactOutcomeConfiguration;
	}

	public ContactAttemptConfiguration getContactAttemptConfiguration() {
		return this.contactAttemptConfiguration;
	}

	public void setContactAttemptConfiguration(ContactAttemptConfiguration contactAttemptConfiguration) {
		this.contactAttemptConfiguration = contactAttemptConfiguration;
	}

	public List<ReferentDto> getReferents() {
		return this.referents;
	}

	public void setReferents(List<ReferentDto> referents) {
		this.referents = referents;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
