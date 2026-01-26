package fr.insee.pearljam.api.service.impl;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

	private final UserRepository userRepository;
	private final CampaignRepository campaignRepository;
	private final UserService userService;

	public HttpStatus setPreferences(List<String> listPreference, String userId) throws CampaignNotFoundException {
		if (listPreference == null) {
			log.error("list of preferences to update shouldn't be null ");
			return HttpStatus.BAD_REQUEST;
		}
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		if (user.isEmpty()) {
			log.error("User {} not found", userId);
			return HttpStatus.NOT_FOUND;
		}
		List<Campaign> lstCampaign = new ArrayList<>();

		for (String campaignId : listPreference) {
			Optional<Campaign> campaign = campaignRepository.findById(campaignId);
			if (campaign.isEmpty()) {
				log.error(Constants.ERR_CAMPAIGN_NOT_EXIST, campaignId);
				return HttpStatus.NOT_FOUND;
			}
			userService.checkUserAssociationToCampaign(campaignId, userId);
			lstCampaign.add(campaign.get());
		}
		user.get().setCampaigns(lstCampaign);
		userRepository.save(user.get());
		return HttpStatus.OK;
	}
}
