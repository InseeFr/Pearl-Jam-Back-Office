package fr.insee.pearljam.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.UserRepository;

@Service
public class PreferenceServiceImpl implements PreferenceService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceServiceImpl.class);

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CampaignRepository campaignRepository;

	public HttpStatus setPreferences(List<String> listPreference, String userId) {
		if(listPreference == null || listPreference.isEmpty()) {
			LOGGER.error("list of campaign to update is empty ");
			return HttpStatus.BAD_REQUEST;
		}
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		if(user.isPresent()) {
			List<Campaign> lstCampaign=new ArrayList<>();
			for(String campaignId : listPreference) {
				Optional<Campaign> campaign = campaignRepository.findById(campaignId);
				if(campaign.isPresent()) {
					lstCampaign.add(campaign.get());
				} else {
					LOGGER.error("Campaign {} not found for user {}", campaignId, userId);
					return HttpStatus.NOT_FOUND;
				}
			}
			user.get().setCampaigns(lstCampaign);
			userRepository.save(user.get());
			return HttpStatus.OK;
		}else {
			LOGGER.error("User {} not found", userId);
			return HttpStatus.NOT_FOUND;
		}
		
	}

}
