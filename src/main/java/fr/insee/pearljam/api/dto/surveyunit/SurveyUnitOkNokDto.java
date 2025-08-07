package fr.insee.pearljam.api.dto.surveyunit;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitOkNokDto {

	List<SurveyUnitOkNokResponseDto> surveyUnitOK;
	List<SurveyUnitOkNokResponseDto> surveyUnitNOK;

}
