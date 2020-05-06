package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDetailDto;
import fr.insee.pearljam.api.dto.surveyunit.SurveyUnitDto;

public interface SurveyUnitService {

	SurveyUnitDetailDto getSurveyUnitDetail(String id);
	
	List<SurveyUnitDto> getSurveyUnitDto(String idInterviewer);

	HttpStatus updateSurveyUnitDetail(String id, SurveyUnitDetailDto surveyUnitDetailDto);

}
