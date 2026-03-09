package fr.insee.pearljam.domain.campaign.port.in;

import java.util.List;

import fr.insee.pearljam.api.web.exception.NotFoundException;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import org.springframework.http.HttpStatus;

public interface PreferenceService {

	/**
	 * @param listPreference list of campaign ids
	 * @param userId user id
	 * @return {@link HttpStatus}
	 */
	HttpStatus setPreferences(List<String> listPreference, String userId) throws CampaignNotFoundException;

	void deletePreferences(String userId) throws NotFoundException;

}
