package fr.insee.pearljam.contracts.surveyunit.dto.state;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class StateCountCampaignDto {

	private List<StateCountDto> organizationUnits;
	private StateCountDto france;

}
