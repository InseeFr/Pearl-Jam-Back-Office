package fr.insee.pearljam.domain.campaign.service;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.infrastructure.persistence.campaign.entity.CampaignDB;
import fr.insee.pearljam.infrastructure.persistence.organizationunit.entity.UserDB;
import fr.insee.pearljam.api.web.exception.NotFoundException;
import fr.insee.pearljam.domain.campaign.port.out.CampaignRepository;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.campaign.port.in.PreferenceService;
import fr.insee.pearljam.domain.organizationunit.port.out.UserRepository;
import fr.insee.pearljam.domain.organizationunit.port.in.UserService;
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
		Optional<UserDB> user = userRepository.findByIdIgnoreCase(userId);
		if (user.isEmpty()) {
			log.error("User {} not found", userId);
			return HttpStatus.NOT_FOUND;
		}
		List<CampaignDB> lstCampaign = new ArrayList<>();

		for (String campaignId : listPreference) {
			Optional<CampaignDB> campaign = campaignRepository.findById(campaignId);
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

	public void deletePreferences(String userId) throws NotFoundException {
		UserDB user = userRepository.findByIdIgnoreCase(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));
		user.setCampaigns(null);
		userRepository.save(user);
	}
}
