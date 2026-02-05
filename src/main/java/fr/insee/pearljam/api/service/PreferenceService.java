package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

public interface PreferenceService {

	/**
	 * @param listPreference
	 * @param userId
	 * @return {@link HttpStatus}
	 */
	HttpStatus setPreferences(List<String> listPreference, String userId);

}
