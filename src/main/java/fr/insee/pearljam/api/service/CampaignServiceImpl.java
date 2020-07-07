package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.dto.campaign.CampaignDto;
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
	/**
	 * This method check if the Interviewer exist or not in database
	 * @param userId
	 * @return boolean
	 */
	@Override
	public boolean existUser(String userId) {
			return "GUEST".equals(userId) || userRepository.findByIdIgnoreCase(userId).isPresent();
	}
	
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
			campaignDtoReturned.add(campaign);
		}
		return campaignDtoReturned;
	}

}
