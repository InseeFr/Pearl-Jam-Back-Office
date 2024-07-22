package fr.insee.pearljam.api.dto.surveyunit;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitOkNokDto {
	List<SurveyUnitOkNokResponseDto> surveyUnitOK;
	List<SurveyUnitOkNokResponseDto> surveyUnitNOK;
	
	public SurveyUnitOkNokDto(List<SurveyUnitOkNokResponseDto> surveyUnitOK, List<SurveyUnitOkNokResponseDto> surveyUnitNOK) {
		super();
		this.surveyUnitOK = surveyUnitOK;
		this.surveyUnitNOK = surveyUnitNOK;
	}
	
	
	public SurveyUnitOkNokDto() {
		super();
	}


	/**
	 * @return the surveyUnitOK
	 */
	public List<SurveyUnitOkNokResponseDto> getSurveyUnitOK() {
		return surveyUnitOK;
	}
	/**
	 * @param surveyUnitOK the surveyUnitOK to set
	 */
	public void setSurveyUnitOK(List<SurveyUnitOkNokResponseDto> surveyUnitOK) {
		this.surveyUnitOK = surveyUnitOK;
	}
	/**
	 * @return the surveyUnitNOK
	 */
	public List<SurveyUnitOkNokResponseDto> getSurveyUnitNOK() {
		return surveyUnitNOK;
	}
	/**
	 * @param surveyUnitNOK the surveyUnitNOK to set
	 */
	public void setSurveyUnitNOK(List<SurveyUnitOkNokResponseDto> surveyUnitNOK) {
		this.surveyUnitNOK = surveyUnitNOK;
	}
	
	
}
