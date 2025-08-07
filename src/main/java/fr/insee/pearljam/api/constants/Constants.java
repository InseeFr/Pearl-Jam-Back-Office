package fr.insee.pearljam.api.constants;

import java.util.List;

public class Constants {
	private Constants() {
		throw new IllegalStateException("Constants class");
	}

	public static final String INTERVIEWER = "interviewer";
	public static final String REVIEWER = "reviewer";
	public static final String GUEST = "GUEST";
	public static final String AUTHORIZATION = "Authorization";
  public static final String USER = "user";

  public static final String API_QUEEN_SURVEYUNITS_STATEDATA = "/api/survey-units/state-data";

  // API
  public static final String API_ADMIN_SURVEYUNITS = "/api/admin/survey-units";
  public static final String API_ADMIN_CAMPAIGN_ID_SURVEYUNITS = "/api/admin/campaign/{id}/survey-units";
  public static final String API_SURVEYUNITS = "/api/survey-units";
  public static final String API_SURVEYUNITS_INTERVIEWERS = "/api/survey-units/interviewers";
  public static final String API_SURVEYUNITS_CLOSABLE = "/api/survey-units/closable";
  public static final String API_SURVEYUNIT_ID_INTERVIEWER = "/api/interviewer/survey-unit/{id}";
  public static final String API_SURVEYUNIT_ID = "/api/survey-unit/{id}";
  public static final String API_SURVEYUNIT_ID_STATE = "/api/survey-unit/{id}/state/{state}";
  public static final String API_SURVEYUNIT_ID_STATES = "/api/survey-unit/{id}/states";
  public static final String API_SURVEYUNIT_ID_COMMENT = "/api/survey-unit/{id}/comment";
  public static final String API_SURVEYUNIT_ID_VIEWED = "/api/survey-unit/{id}/viewed";
  public static final String API_SURVEYUNIT_ID_CLOSE = "/api/survey-unit/{id}/close/{closingCause}";
  public static final String API_SURVEYUNIT_ID_CLOSINGCAUSE = "/api/survey-unit/{id}/closing-cause/{closingCause}";
  public static final String API_ADMIN_SURVEYUNIT_DETAILS = "/api/admin/survey-unit/{id}";


  public static final String API_SURVEYUNIT_ID_TEMP_ZONE = "/api/survey-unit/{id}/temp-zone";
  public static final String API_SURVEYUNITS_TEMP_ZONE = "/api/survey-units/temp-zone";

  public static final String API_CAMPAIGNS = "/api/campaigns";
  public static final String API_ADMIN_CAMPAIGNS = "/api/admin/campaigns";
  public static final String API_INTERVIEWER_CAMPAIGNS = "/api/interviewer/campaigns";
  public static final String API_CAMPAIGNS_SU_STATECOUNT = "/api/campaigns/survey-units/state-count";
  public static final String API_CAMPAIGNS_SU_CONTACTOUTCOMES = "/api/campaigns/survey-units/contact-outcomes";

  public static final String API_CAMPAIGN = "/api/campaign";
  public static final String API_CAMPAIGN_ID = "/api/campaign/{id}";
  public static final String API_CAMPAIGNS_ID = "/api/campaigns/{id}";
  public static final String API_CAMPAIGN_COLLECTION_DATES = "/api/campaign/{id}/collection-dates";
  public static final String API_CAMPAIGN_ID_INTERVIEWERS = "/api/campaign/{id}/interviewers";
  public static final String API_CAMPAIGN_ID_SURVEYUNITS = "/api/campaign/{id}/survey-units";
  public static final String API_CAMPAIGN_ID_SU_ABANDONED = "/api/campaign/{id}/survey-units/abandoned";
  public static final String API_CAMPAIGN_ID_SU_NOTATTRIBUTED = "/api/campaign/{id}/survey-units/not-attributed";
  public static final String API_CAMPAIGN_ID_SU_STATECOUNT = "/api/campaign/{id}/survey-units/state-count";
  public static final String API_CAMPAIGN_ID_SU_INTERVIEWER_STATECOUNT = "/api/campaign/{id}/survey-units/interviewer/{idep}/state-count";
  public static final String API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_STATECOUNT = "/api/campaign/{id}/survey-units/not-attributed/state-count";
  public static final String API_CAMPAIGN_ID_SU_CONTACTOUTCOMES = "/api/campaign/{id}/survey-units/contact-outcomes";
  public static final String API_CAMPAIGN_ID_SU_INTERVIEWER_CONTACTOUTCOMES = "/api/campaign/{id}/survey-units/interviewer/{idep}/contact-outcomes";
  public static final String API_CAMPAIGN_ID_SU_NOT_ATTRIBUTED_CONTACTOUTCOMES = "/api/campaign/{id}/survey-units/not-attributed/contact-outcomes";
  public static final String API_CAMPAIGN_ID_SU_INTERVIEWER_CLOSINGCAUSES = "/api/campaign/{id}/survey-units/interviewer/{idep}/closing-causes";
  public static final String API_CAMPAIGN_ID_OU_ID_VISIBILITY = "/api/campaign/{idCampaign}/organizational-unit/{idOu}/visibility";
  public static final String API_CAMPAIGN_ID_OU_ID_COMMUNICATION_INFORMATION = "/api/campaign/{idCampaign}/organizational-unit/{idOu}/communication-information";
  public static final String API_CAMPAIGN_ID_VISIBILITIES = "/api/campaign/{id}/visibilities";
  public static final String API_CAMPAIGN_ID_COMMUNICATION_TEMPLATES = "/api/campaign/{id}/communication-templates";
  public static final String API_CAMPAIGN_ID_COMMUNICATION_INFORMATIONS = "/api/campaign/{id}/communication-informations";
  public static final String API_CAMPAIGN_ID_REFERENTS = "/api/campaigns/{id}/referents";
  public static final String API_CAMPAIGNS_ID_ON_GOING = "/campaigns/{id}/ongoing";
  public static final String API_CAMPAIGNS_ON_GOING = "/api/campaigns/ongoing";
  public static final String API_CAMPAIGN_ID_INTERVIEWERS_STATECOUNT = "api/campaign/{id}/interviewers/state-count";

  public static final String API_INTERVIEWERS = "/api/interviewers";
  public static final String API_INTERVIEWERS_SU_STATECOUNT = "/api/interviewers/survey-units/state-count";
  public static final String API_INTERVIEWER_ID = "/api/interviewer/{id}";
  public static final String API_INTERVIEWER_ID_CAMPAIGNS = "/api/interviewer/{id}/campaigns";
  public static final String API_ADMIN_INTERVIEWERS = "/api/admin/interviewers";

  public static final String API_USER = "/api/user";
  public static final String API_USER_ID = "/api/user/{id}";
  public static final String API_USER_ID_ORGANIZATIONUNIT_ID = "/api/user/{id}/organization-unit/{id2}";
  public static final String API_USER_ID_ORGANIZATION_ID_OUID = "/api/user/{userId}/organization-unit/{ouId}";
  public static final String API_ORGANIZATIONUNIT = "/api/organization-unit";
  public static final String API_ORGANIZATIONUNITS = "/api/organization-units";
  public static final String API_ORGANIZATIONUNIT_ID = "/api/organization-unit/{id}";
  public static final String API_ORGANIZATIONUNIT_ID_USERS = "/api/organization-unit/{id}/users";

  public static final String API_PREFERENCES = "/api/preferences";

  public static final String API_MESSAGE = "/api/message";
  public static final String API_MESSAGES_ID = "/api/messages/{id}";
  public static final String API_VERIFYNAME = "/api/verify-name";
  public static final String API_MESSAGEHISTORY = "/api/message-history";
  public static final String API_MESSAGE_MARK_AS_READ = "/api/message/{id}/interviewer/{idep}/read";
  public static final String API_MESSAGE_MARK_AS_DELETED = "/api/message/{id}/interviewer/{idep}/delete";

  public static final String API_CREATEDATASET = "/api/create-dataset";
  public static final String API_DELETEDATASET = "/api/delete-dataset";

  public static final String API_CHECK_HABILITATION = "/api/check-habilitation";

  public static final String API_HEALTH_CHECK = "/api/healthcheck";

  public static final String API_MAIL = "/api/mail";

  public static final String API_ENUM_STATE = "/api/enum/state";
  public static final String API_ENUM_CONTACT_OUTCOME = "/api/enum/contact-outcome";
  public static final String API_ENUM_CONTACT_ATTEMPT = "/api/enum/contact-attempt";
  // Error messages
  public static final String ERR_USER_NOT_EXIST = "User {} does not exist";
  public static final String ERR_CAMPAIGN_NOT_EXIST = "Campaign {} does not exist";
  public static final String ERR_NO_OU_FOR_CAMPAIGN = "There is no Organisation Unit for Campaign {} affiliated to user {}";

  // Front expected variables names

  public static final String NVM_COUNT = "nvmCount";
  public static final String NNS_COUNT = "nnsCount";
  public static final String ANV_COUNT = "anvCount";
  public static final String VIN_COUNT = "vinCount";
  public static final String VIC_COUNT = "vicCount";
  public static final String PRC_COUNT = "prcCount";
  public static final String AOC_COUNT = "aocCount";
  public static final String APS_COUNT = "apsCount";
  public static final String INS_COUNT = "insCount";
  public static final String WFT_COUNT = "wftCount";
  public static final String WFS_COUNT = "wfsCount";
  public static final String TBR_COUNT = "tbrCount";
  public static final String FIN_COUNT = "finCount";
  public static final String CLO_COUNT = "cloCount";
  public static final String NVA_COUNT = "nvaCount";
  public static final String TOTAL_COUNT = "total";

  public static final String NPA_COUNT = "npaCount";
  public static final String NPI_COUNT = "npiCount";
  public static final String NPX_COUNT = "npxCount";
  public static final String ROW_COUNT = "rowCount";

  public static final String INA_COUNT = "inaCount";
  public static final String REF_COUNT = "refCount";
  public static final String IMP_COUNT = "impCount";
  public static final String UCD_COUNT = "ucdCount";
  public static final String UTR_COUNT = "utrCount";
  public static final String ALA_COUNT = "alaCount";
  public static final String NUH_COUNT = "nuhCount";
  public static final String DUK_COUNT = "dukCount";
  public static final String DUU_COUNT = "duuCount";
  public static final String NOA_COUNT = "noaCount";
  public static final String NOTICE_COUNT = "noticeCount";
  public static final String REMINDER_COUNT = "reminderCount";

  public static final List<String> STATE_COUNT_FIELDS = List.of(NVM_COUNT, NNS_COUNT, ANV_COUNT,
      VIN_COUNT, VIC_COUNT, PRC_COUNT, AOC_COUNT, APS_COUNT, INS_COUNT, WFT_COUNT, WFS_COUNT, TBR_COUNT, FIN_COUNT,
      CLO_COUNT, NVA_COUNT, TOTAL_COUNT, NOTICE_COUNT, REMINDER_COUNT);

  public static final List<String> STATECOUNT_CLOSED_CLOSING_CAUSE_FIELDS = List.of(NPA_COUNT,
      NPI_COUNT, NPX_COUNT, ROW_COUNT);

  public static final List<String> CLOSING_CAUSE_FIELDS = List.of(NPA_COUNT, NPI_COUNT, NPX_COUNT,
      ROW_COUNT, TOTAL_COUNT);

  public static final List<String> CONTACT_OUTCOME_FIELDS = List.of(INA_COUNT, REF_COUNT, IMP_COUNT,
      UCD_COUNT, UTR_COUNT, ALA_COUNT, NUH_COUNT, DUK_COUNT, DUU_COUNT, NOA_COUNT);

  public static final String UNAVAILABLE = "UNAVAILABLE";

}
