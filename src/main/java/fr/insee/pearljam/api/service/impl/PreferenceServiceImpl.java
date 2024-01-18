package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.Campaign;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

	private final UserRepository userRepository;
	private final CampaignRepository campaignRepository;
	private final UserService userService;

	public HttpStatus setPreferences(List<String> listPreference, String userId) {
		if (listPreference == null) {
			log.error("list of preferences to update is null ");
			return HttpStatus.BAD_REQUEST;
		}
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);
		if (!user.isPresent()) {
			log.error("User {} not found", userId);
			return HttpStatus.NOT_FOUND;
		}
		List<Campaign> lstCampaign = new ArrayList<>();

		for (String campaignId : listPreference) {
			Optional<Campaign> campaign = campaignRepository.findById(campaignId);
			if (!campaign.isPresent()) {
				log.error(Constants.ERR_CAMPAIGN_NOT_EXIST, campaignId);
				return HttpStatus.NOT_FOUND;
			}
			if (!userService.isUserAssocitedToCampaign(campaignId, userId) && !Constants.GUEST.equals(userId)) {
				log.error(Constants.ERR_NO_OU_FOR_CAMPAIGN, campaignId, userId);
				return HttpStatus.BAD_REQUEST;
			}
			lstCampaign.add(campaign.get());
		}
		user.get().setCampaigns(lstCampaign);
		userRepository.save(user.get());
		return HttpStatus.OK;
	}
}
