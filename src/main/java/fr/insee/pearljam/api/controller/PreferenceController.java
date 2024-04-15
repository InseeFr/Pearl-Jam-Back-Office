package fr.insee.pearljam.api.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.web.authentication.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api")
public class PreferenceController {

	private final PreferenceService preferenceService;

	private final AuthenticationHelper authHelper;

	/**
	 * This method is using to update the state of Survey Units listed in request
	 * body
	 * 
	 * @param request
	 * @param listPreference
	 * @return
	 */
	@Operation(summary = "Update preferences with campaigns listed in request body")
	@PutMapping(path = "/preferences")
	public ResponseEntity<Object> updateSurveyUnit(Authentication auth,
			@RequestBody List<String> listPreference) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			HttpStatus returnCode = preferenceService.setPreferences(listPreference, userId);
			log.info("PUT preferences '{}' for user {} resulting in {}", String.join(", ", listPreference), userId,
					returnCode.value());
			return new ResponseEntity<>(returnCode);
		}
	}
}
