package fr.insee.pearljam.api.dto.state;

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
