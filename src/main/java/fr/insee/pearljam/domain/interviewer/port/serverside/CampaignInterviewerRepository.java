package fr.insee.pearljam.domain.interviewer.port.serverside;

import fr.insee.pearljam.domain.interviewer.model.CampaignInterviewer;

import java.util.List;

public interface CampaignInterviewerRepository {
	List<CampaignInterviewer> findCampaignInterviewers(String campaignId, List<String> organizationUnitIds);
}
