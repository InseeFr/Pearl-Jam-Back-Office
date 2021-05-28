package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.service.UserService;

@Service
public class PreferenceServiceImpl implements PreferenceService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceServiceImpl.class);

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CampaignRepository campaignRepository;
	
	@Autowired
	UserService userService;

	public HttpStatus setPreferences(List<String> listPreference, String userId) {
		if(listPreference == null) {
			LOGGER.error("list of campaign to update is empty ");
			return HttpStatus.BAD_REQUEST;
		}
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		if(!user.isPresent()) {
			LOGGER.error("User {} not found", userId);
			return HttpStatus.NOT_FOUND;
		}
    List<Campaign> lstCampaign = new ArrayList<>();

		for(String campaignId : listPreference) {
			Optional<Campaign> campaign = campaignRepository.findById(campaignId);
			if(!campaign.isPresent()) {
				LOGGER.error(Constants.ERR_CAMPAIGN_NOT_EXIST, campaignId);
				return HttpStatus.NOT_FOUND;
			}
			if(!userService.isUserAssocitedToCampaign(campaignId, userId)  && !Constants.GUEST.equals(userId)){
				LOGGER.error(Constants.ERR_NO_OU_FOR_CAMPAIGN, campaignId, userId);
				return HttpStatus.BAD_REQUEST;
			}
			lstCampaign.add(campaign.get());
		}
		user.get().setCampaigns(lstCampaign);
		userRepository.save(user.get());
		return HttpStatus.OK;
	}
}
