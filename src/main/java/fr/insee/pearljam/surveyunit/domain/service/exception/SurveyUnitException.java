package fr.insee.pearljam.surveyunit.domain.service.exception;

import java.io.Serial;

public class SurveyUnitException extends Exception {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 5447456941599467523L;
	
	public SurveyUnitException(String message) {
		super(message);
	}

	public SurveyUnitException() {
		super();
	}

}
