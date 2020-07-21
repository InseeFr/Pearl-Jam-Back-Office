package fr.insee.pearljam.api.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}

	public static final String INTERVIEWER = "interviewer";
	public static final String USER = "user";
	public static final String OU_NORTH = "OU-NORTH";
	public static final String OU_SOUTH = "OU-SOUTH";
	public static final String OU_EAST = "OU-EAST";
	public static final String OU_WEST = "OU-WEST";
  	public static final List<String> LIST_LABELS_OU = new ArrayList<>(Arrays.asList("OU-NORTH", "OU-SOUTH", "OU-EAST", "OU-WEST"));
	public static final String API_SURVEYUNITS = "/api/survey-units";
	public static final String API_SURVEYUNITS_ID = "/api/survey-unit/{id}";
	public static final String API_CAMPAIGN_ID_INTERVIEWERS = "/api/campaigns/{id}/interviewers";
	public static final String API_CAMPAIGN_ID_SURVEYUNITS = "/api/campaign/{id}/survey-units";
	public static final String API_CAMPAIGN = "/api/campaigns";
	public static final String API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT = "/api/campaigns/{id}/survey-units/interviewer/{idep}/state-count";
	public static final String API_CAMPAIGN_ID_SU_STATECOUNT = "/api/campaign/{id}/survey-units/state-count";
	public static final String API_USER= "/api/user";
}
