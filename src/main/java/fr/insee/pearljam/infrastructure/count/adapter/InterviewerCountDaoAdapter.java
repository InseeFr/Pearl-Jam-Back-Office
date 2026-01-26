package fr.insee.pearljam.infrastructure.count.adapter;

import fr.insee.pearljam.domain.count.model.InterviewerCount;
import fr.insee.pearljam.domain.count.port.serverside.InterviewerCountRepository;
import fr.insee.pearljam.infrastructure.count.jpa.InterviewerCountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InterviewerCountDaoAdapter implements InterviewerCountRepository {

    private final InterviewerCountJpaRepository interviewerCountJpaRepository;

    @Override
    public List<InterviewerCount> findCampaignInterviewers(String campaignId, List<String> organizationUnitIds) {
        return interviewerCountJpaRepository.findInterviewerCountByCampaignIdAndOuId(campaignId, organizationUnitIds);
    }
}
