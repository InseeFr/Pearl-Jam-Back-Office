package fr.insee.pearljam.api.constants;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}

	public static final String INTERVIEWER = "interviewer";
	public static final String USER = "user";
	public static final String API_SURVEYUNITS = "/api/survey-units";
	public static final String API_SURVEYUNITS_ID = "/api/survey-unit/{id}";
	public static final String API_CAMPAIGN_ID_INTERVIEWERS = "/api/campaigns/{id}/interviewers";
	public static final String API_CAMPAIGN_ID_SURVEYUNITS = "/api/campaign/{id}/survey-units";
	public static final String API_CAMPAIGN = "/api/campaigns";
	public static final String API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT = "/api/campaigns/{id}/survey-units/interviewer/{idep}/state-count";
	public static final String API_USER= "/api/user";
}
