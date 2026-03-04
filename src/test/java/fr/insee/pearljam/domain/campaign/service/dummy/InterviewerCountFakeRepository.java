package fr.insee.pearljam.domain.campaign.service.dummy;

import fr.insee.pearljam.domain.count.model.InterviewerCount;
import fr.insee.pearljam.domain.count.port.serverside.InterviewerCountRepository;

import java.util.List;

public class InterviewerCountFakeRepository implements InterviewerCountRepository {
    @Override
    public List<InterviewerCount> findCampaignInterviewers(String campaignId, List<String> organizationUnitIds) {
        return List.of();
    }
}