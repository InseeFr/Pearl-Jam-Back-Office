package fr.insee.pearljam.api.dto.campaign;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import fr.insee.pearljam.api.dto.visibility.VisibilityContextDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignContextDto {
	private String campaign;
	private String campaignLabel;
	private List<VisibilityContextDto> visibilities;
	
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

	
	

}
