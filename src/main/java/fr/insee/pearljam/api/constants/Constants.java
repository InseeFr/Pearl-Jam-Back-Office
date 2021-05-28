package fr.insee.pearljam.api.constants;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}

	
	public static final String INTERVIEWER = "interviewer";
	public static final String USER = "user";
  public static final String GUEST = "GUEST";
  
  public static final String API_CHECK_HABILITATION = "/api/check-habilitation";

	// API for interviewers
	public static final String API_SURVEYUNITS = "/api/survey-units";
	public static final String API_SURVEYUNITS_INTERVIEWERS = "/api/survey-units/interviewers";
	public static final String API_SURVEYUNIT_ID = "/api/survey-unit/{id}";

	// API for users
	public static final String API_SURVEYUNITS_STATE= "/api/survey-unit/{id}/state/{state}";
	public static final String API_SURVEYUNIT_ID_STATES = "/api/survey-unit/{id}/states";
	public static final String API_SURVEYUNIT_ID_COMMENT = "/api/survey-unit/{id}/comment";
	public static final String API_SURVEYUNIT_ID_VIEWED = "/api/survey-unit/{id}/viewed";
	public static final String API_SURVEYUNIT_CLOSABLE = "/api/survey-units/closable";
	
  public static final String API_CAMPAIGNS = "/api/campaigns";
  public static final String API_CAMPAIGNS_STATE_COUNT = "/api/campaigns/survey-units/state-count";
  public static final String API_CAMPAIGN_COLLECTION_DATES = "/api/campaign/{id}/collection-dates";
	public static final String API_CAMPAIGN_ID_INTERVIEWERS = "/api/campaign/{id}/interviewers";
	public static final String API_CAMPAIGN_ID_SURVEYUNITS = "/api/campaign/{id}/survey-units";
	public static final String API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT = "/api/campaign/{id}/survey-units/interviewer/{idep}/state-count";
	public static final String API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT = "/api/campaign/{id}/survey-units/not-attributed/state-count";
	public static final String API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_CONTACT_OUTCOMES = "/api/campaign/{id}/survey-units/not-attributed/contact-outcomes";

	public static final String API_CAMPAIGN_ID_SU_STATECOUNT = "/api/campaign/{id}/survey-units/state-count";
	public static final String API_CAMPAIGN_ID_SU_NOTATTRIBUTED = "/api/campaign/{id}/survey-units/not-attributed";
	public static final String API_CAMPAIGN_ID_SU_ABANDONED = "/api/campaign/{id}/survey-units/abandoned";
	public static final String API_CAMPAIGN_ID_OU_ID_VISIBILITY = "/api/campaign/{id}/organizational-unit/{id}/visibility";
	public static final String API_CAMPAIGN_CONTACT_OUTCOME = "/api/campaigns/survey-units/contact-outcomes";
	public static final String API_CAMPAIGN_SU_CONTACT_OUTCOME = "/api/campaign/{id}/survey-units/contact-outcomes";
	
	public static final String API_INTERVIEWERS = "/api/interviewers";
	public static final String API_INTERVIEWERS_CAMPAIGN = "/api/interviewer/{id}/campaigns";
	public static final String API_INTERVIEWERS_STATE_COUNT = "/api/interviewers/survey-units/state-count";
	public static final String API_INTERVIEWERS_CONTACT_OUTCOME_COUNT = "/api/campaign/{id}/survey-units/interviewer/{idep}/contact-outcomes";	

	public static final String API_USER= "/api/user";
	public static final String API_GEOGRAPHICALLOCATIONS= "/api/geographical-locations";
	public static final String API_PREFERENCES = "/api/preferences";
  
  	public static final String API_MESSAGE = "/api/message";
  	public static final String API_GET_MESSAGES = "/api/messages/{id}";
  	public static final String API_VERIFY = "/api/verify-name";
  	public static final String API_MESSAGE_HISTORY = "/api/message-history";
  	public static final String API_MESSAGE_MARK_AS_READ = "/api/message/{id}/interviewer/{idep}/read";
  	public static final String API_MESSAGE_MARK_AS_DELETED = "/api/message/{id}/interviewer/{idep}/delete";
  
    public static final String API_CREATE_DATASET = "/api/create-dataset";
    public static final String API_DELETE_DATASET = "/api/delete-dataset";
    
    public static final String API_SURVEYUNIT_CLOSE = "/api/survey-unit/{id}/close/{closingCause}";
    public static final String API_SURVEYUNIT_CLOSING_CAUSE = "/api/survey-unit/{id}/closing-cause/{closingCause}";

    public static final String API_CAMPAIGN = "/api/campaign";
    public static final String API_OU_CONTEXT = "/api/organization-units";

  	
	// Error messages
	public static final String ERR_USER_NOT_EXIST = "User {} does not exist";
	public static final String ERR_CAMPAIGN_NOT_EXIST = "Campaign {} does not exist";
	public static final String ERR_NO_OU_FOR_CAMPAIGN = "There is no Organisation Unit for Campaign {} affiliated to user {}";
	
	public static final String[] STATE_COUNT_FIELDS = {
			"nvmCount",
			"nnsCount",
			"anvCount",
			"vinCount",
			"vicCount",
			"prcCount",
			"aocCount",
			"apsCount",
			"insCount",
			"wftCount",
			"wfsCount",
			"tbrCount",
			"finCount",
			"qnaCount",
			"qnaFinCount",
			"nvaCount",
			"total"
	};
	
	public static final String[] CLOSING_CAUSE_FIELDS = {
			"npaCount",
			"npiCount",
			"rowCount"
	};
	
	public static final String[] CONTACT_OUTCOME_FIELDS = {
			"inaCount",
			"refCount",
			"impCount",
			"iniCount",
			"alaCount",
			"wamCount",
			"oosCount"
	};
	
	
	

}
