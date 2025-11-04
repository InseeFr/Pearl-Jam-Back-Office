package fr.insee.pearljam.surveyunit.infrastructure.rest.dto.statedata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StateDataDto {
	private String state;
	private Long date;
	private String currentPage;

}
