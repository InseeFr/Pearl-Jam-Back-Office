package fr.insee.pearljam.organization.infrastructure.rest.controller;

import fr.insee.pearljam.configuration.web.Constants;
import fr.insee.pearljam.organization.domain.port.userside.PreferenceService;
import fr.insee.pearljam.security.domain.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PreferenceController {

	private final PreferenceService preferenceService;
	private final AuthenticatedUserService authenticatedUserService;

	/**
	 * This method is using to update the state of Survey Units listed in request
	 * body
	 * 
	 * @param listPreference list
	 * @return void
	 */
	@Operation(summary = "Update current user preferences with campaigns listed in request body")
	@PutMapping(Constants.API_PREFERENCES)
	public ResponseEntity<Object> updatePreferences(@RequestBody List<String> listPreference) {
		String userId = authenticatedUserService.getCurrentUserId();
		HttpStatus returnCode = preferenceService.setPreferences(listPreference, userId);
		log.info("PUT preferences '{}' for user {} resulting in {}", String.join(", ", listPreference), userId,
				returnCode.value());
		return new ResponseEntity<>(returnCode);
	}
}
