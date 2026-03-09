package fr.insee.pearljam.domain.preference.port.userside;

import java.util.List;

import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.domain.exception.CampaignNotFoundException;
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
