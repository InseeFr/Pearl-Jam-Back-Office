package fr.insee.pearljam.api.contactoutcome.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ContactOutcomeTypeCountCampaignDto {

	private List<ContactOutcomeTypeCountDto> organizationUnits;
	private ContactOutcomeTypeCountDto france;

}
