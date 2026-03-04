package fr.insee.pearljam.api.dto.state;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class StateCountCampaignDto {
	
	private List<StateCountDto> organizationUnits;
	private StateCountDto france;

}
