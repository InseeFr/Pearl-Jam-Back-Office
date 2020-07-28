package fr.insee.pearljam.api.service;

import java.util.List;

import org.springframework.http.HttpStatus;

public interface PreferenceService {

	public HttpStatus setPreferences(List<String> listPreference, String userId);

}
