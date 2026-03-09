package fr.insee.pearljam.api.surveyunit.dto.state;

import fr.insee.pearljam.contracts.surveyunit.dto.state.StateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class SurveyUnitStatesDto {
	private String id;
	private List<StateDto> states;

}
