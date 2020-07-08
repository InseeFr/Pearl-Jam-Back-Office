package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
import fr.insee.pearljam.api.dto.interviewer.InterviewerDto;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.UserRepository;

/**
 * Implementation of the Service for the Interviewer entity
 * @author scorcaud
 *
 */
@Service
public class CampaignServiceImpl implements CampaignService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignServiceImpl.class);
	
	private static final String GUEST = "GUEST";
	
	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	UserRepository userRepository;
	
	public List<CampaignDto> getListCampaign(String userId) {
		List<CampaignDto> campaignDtoReturned = new ArrayList<>();
		List<String> campaignDtoIds = null;
		if(userId.equals(GUEST)) {
			campaignDtoIds = campaignRepository.findAllIds();
		} else {
			campaignDtoIds = campaignRepository.findIdsByUserId(userId);
		}
		if(campaignDtoIds.isEmpty()) {
			LOGGER.error("No campaign found for user {}", userId);
			return List.of();
		}
		for(String idCampaign : campaignDtoIds) {
			CampaignDto campaign = campaignRepository.findDtoById(idCampaign);
			campaign.setAffected(0L);
			campaign.setInProgress(0L);
			campaign.setToAffect(0L);
			campaign.setToControl(0L);
			campaignDtoReturned.add(campaign);
		}
		return campaignDtoReturned;
	}

  public List<InterviewerDto> getListInterviewers(String userId, String campaignId) {
		List<InterviewerDto> interviewersDtoReturned = new ArrayList<>();
		if(userId.equals(GUEST) || isUserAssociatedToTheCampaign(userId, campaignId)) {
      interviewersDtoReturned = campaignRepository.findInterviewersDtoByCampaignId(campaignId);
		}
		if(interviewersDtoReturned.isEmpty()) {
			LOGGER.error("No interviewers found for the user {} and the campaign {}", userId, campaignId);
			return List.of();
		}
		return interviewersDtoReturned;
	}

  public boolean isUserAssociatedToTheCampaign(String userId, String campaignId){
  	return !(campaignRepository.checkCampaignPreferences(userId, campaignId).isEmpty());
  }
}
