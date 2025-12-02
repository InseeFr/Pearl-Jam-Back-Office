package fr.insee.pearljam.infrastructure.interviewer.adapter;

import fr.insee.pearljam.domain.interviewer.model.CampaignInterviewer;
import fr.insee.pearljam.domain.interviewer.port.serverside.CampaignInterviewerRepository;
import fr.insee.pearljam.infrastructure.interviewer.jpa.CampaignInterviewerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CampaignInterviewerDaoAdapter implements CampaignInterviewerRepository {

	private final CampaignInterviewerJpaRepository campaignInterviewerJpaRepository;

	@Override
	public List<CampaignInterviewer> findCampaignInterviewers(String campaignId, List<String> organizationUnitIds) {
		return campaignInterviewerJpaRepository.findCampaignInterviewers(campaignId, organizationUnitIds);
	}
}
