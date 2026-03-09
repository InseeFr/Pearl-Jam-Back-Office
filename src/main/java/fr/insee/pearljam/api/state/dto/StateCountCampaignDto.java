package fr.insee.pearljam.api.state.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class StateCountCampaignDto {

	private List<StateCountDto> organizationUnits;
	private StateCountDto france;

}
