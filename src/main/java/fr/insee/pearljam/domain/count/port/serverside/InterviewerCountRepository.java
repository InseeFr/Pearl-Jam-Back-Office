package fr.insee.pearljam.domain.count.port.serverside;

import fr.insee.pearljam.domain.count.model.InterviewerCount;

import java.util.List;

public interface InterviewerCountRepository {
    List<InterviewerCount> findCampaignInterviewers(String campaignId, List<String> organizationUnitIds);
}
