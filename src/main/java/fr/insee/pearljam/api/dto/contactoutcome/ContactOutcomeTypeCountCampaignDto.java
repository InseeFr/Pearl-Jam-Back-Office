package fr.insee.pearljam.api.dto.contactoutcome;

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
