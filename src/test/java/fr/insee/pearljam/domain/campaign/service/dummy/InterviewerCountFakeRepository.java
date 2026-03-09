package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;
import fr.insee.pearljam.domain.surveyunit.port.out.InterviewerCountRepository;

import java.util.List;

public class InterviewerCountFakeRepository implements InterviewerCountRepository {
    @Override
    public List<InterviewerCount> findCampaignInterviewers(String campaignId, List<String> organizationUnitIds) {
        return List.of();
    }
}