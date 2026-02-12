package fr.insee.pearljam.api.dto.campaign;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignPreferenceDto {
	private final String id;
	private final String label;
	private final boolean preference;
}
