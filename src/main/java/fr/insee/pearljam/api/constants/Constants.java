package fr.insee.pearljam.api.constants;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}

	
	public static final String INTERVIEWER = "interviewer";
	public static final String USER = "user";
	public static final String GUEST = "GUEST";

	// API for interviewers
	public static final String API_SURVEYUNITS = "/api/survey-units";
	public static final String API_SURVEYUNIT_ID = "/api/survey-unit/{id}";

	// API for users
	public static final String API_SURVEYUNITS_STATE= "/api/survey-unit/{id}/state/{state}";
	public static final String API_SURVEYUNIT_ID_STATES = "/api/survey-unit/{id}/states";
	
  public static final String API_CAMPAIGNS = "/api/campaigns";
  public static final String API_CAMPAIGNS_STATE_COUNT = "/api/campaigns/survey-units/state-count";
  public static final String API_INTERVIEWERS_STATE_COUNT = "/api/interviewers/survey-units/state-count";
	public static final String API_CAMPAIGN_ID_INTERVIEWERS = "/api/campaign/{id}/interviewers";
	public static final String API_CAMPAIGN_ID_SURVEYUNITS = "/api/campaign/{id}/survey-units";
	public static final String API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT = "/api/campaign/{id}/survey-units/interviewer/{idep}/state-count";
	public static final String API_CAMPAIGN_ID_SU_STATECOUNT = "/api/campaign/{id}/survey-units/state-count";
	public static final String API_CAMPAIGN_ID_SU_NOTATTRIBUTED = "/api/campaign/{id}/survey-units/not-attributed";
	public static final String API_CAMPAIGN_ID_SU_ABANDONED = "/api/campaign/{id}/survey-units/abandoned";
	
	public static final String API_USER= "/api/user";
	public static final String API_PREFERENCES = "/api/preferences";
	
	
	// Error messages
	public static final String ERR_USER_NOT_EXIST = "User {} does not exist";
	public static final String ERR_CAMPAIGN_NOT_EXIST = "Campaign {} does not exist";
	public static final String ERR_NO_OU_FOR_CAMPAIGN = "There is no Organisation Unit for Campaign {} affiliated to user {}";
}
