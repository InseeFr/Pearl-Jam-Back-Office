package fr.insee.pearljam.domain.campaign.port.out;

import fr.insee.pearljam.domain.reporting.readmodel.Interviewer;
import fr.insee.pearljam.domain.reporting.readmodel.Referent;
import fr.insee.pearljam.domain.reporting.readmodel.SurveyUnitsCampaignOrganization;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignWithVisibility;

import java.util.List;

public interface CampaignOrganizationRepository {

    CampaignWithVisibility findCampaignVisibility(String campaignId);
    SurveyUnitsCampaignOrganization getSurveyUnitsCampaignOrganizations();

    List<Referent> getReferents(String campaignId);
    List<Interviewer> getInterviewers(String campaignId);
}
