package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;

import java.util.List;

public interface InterviewerCountRepository {
    List<InterviewerCount> findCampaignInterviewers(String campaignId, List<String> organizationUnitIds);
}
