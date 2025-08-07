package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.MessageService;
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "03. Users", description = "Endpoints for users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final MessageService messageService;
	private final OrganizationUnitService organizationUnitService;
	private final PreferenceService preferenceService;
	private final AuthenticatedUserService authenticatedUserService;

	/**
	 * This method returns the current USER
	 * 
	 * @param request
	 * @return List of {@link UserDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get User")
	@GetMapping(Constants.API_USER)
	public ResponseEntity<UserDto> getUser() {
		String userId = authenticatedUserService.getCurrentUserId();
		Optional<UserDto> user = userService.getUser(userId);
		if (user.isEmpty()) {
			log.info("GET User resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("GET User resulting in 200");
		return new ResponseEntity<>(user.get(), HttpStatus.OK);
	}

	/**
	 * This method returns user matching with the `id` param
	 * 
	 * @param request
	 * @return List of {@link UserDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get User by id")
	@GetMapping(Constants.API_USER_ID)
	public ResponseEntity<UserDto> getUserById(
						@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to GET user with id : {}", userId, id);
		Optional<UserDto> user = userService.getUser(id);
		if (user.isEmpty()) {
			log.info("GET User resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("GET User resulting in 200");
		return new ResponseEntity<>(user.get(), HttpStatus.OK);
	}

	/**
	 * This method creates a user
	 * 
	 * @param request
	 */
	@Operation(summary = "Create User")
	@PostMapping(Constants.API_USER)
	public ResponseEntity<Object> createUser(
						@RequestBody UserDto user) {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to create a new user", userId);
		if (!userService.checkValidity(user)) {
			String invalidUserInfo = String.format("Invalid user : %s", user.toString());
			log.info(invalidUserInfo);
			return new ResponseEntity<>(invalidUserInfo, HttpStatus.BAD_REQUEST);
		}

		if (userService.userIsPresent(user.getId())) {
			String alreadyPresentUserInfo = String.format("User %s can't be created : already present",
					user.toString());
			log.warn(alreadyPresentUserInfo);
			return new ResponseEntity<>(alreadyPresentUserInfo, HttpStatus.CONFLICT);
		}

		UserDto createdUser;
		try {
			createdUser = userService.createUser(user);
			log.info("User {} created", user.getId());
		} catch (Exception e) {
			String unexpectedError = String.format("Exception when creating User %s", user.getId());
			log.warn(unexpectedError, e);
			return new ResponseEntity<>(unexpectedError, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	/**
	 * This method updates a user
	 * 
	 * @param request
	 */
	@Operation(summary = "Update User")
	@PutMapping(Constants.API_USER_ID)
	public ResponseEntity<Object> updateUser(
			@PathVariable(value = "id") String id,
			@RequestBody UserDto user) {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to update user {}", userId, id);

		if (!userService.checkValidity(user)) {
			String invalidUserInfo = String.format("Invalid user : %s", user.toString());
			log.info(invalidUserInfo);
			return new ResponseEntity<>(invalidUserInfo, HttpStatus.BAD_REQUEST);
		}

		if (!userService.userIsPresent(id)) {
			String noFoundUser = String.format("User %s can't be updated : not found", id);
			log.warn(noFoundUser);
			return new ResponseEntity<>(noFoundUser, HttpStatus.NOT_FOUND);
		}

		if (!id.equals(user.getId())) {
			String differentUser = String.format("User %s can't be updated : provided Id [%s] is different", id,
					user.getId());
			log.warn(differentUser);
			return new ResponseEntity<>(differentUser, HttpStatus.CONFLICT);
		}

		UserDto updatedUser;
		try {
			updatedUser = userService.updateUser(user);
		} catch (NotFoundException e) {
			String noFoundUser = String.format("User %s can't be updated : not found", id);
			log.warn(noFoundUser);
			return new ResponseEntity<>(noFoundUser, HttpStatus.NOT_FOUND);
		}
		log.info("{} updated user {} - {} ", userId, id, HttpStatus.OK.value());
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	/**
	 * This method assign a user to target Organization Unit
	 * 
	 * @param request
	 */
	@Operation(summary = "Assign User to Organization Unit")
	@PutMapping(Constants.API_USER_ID_ORGANIZATION_ID_OUID)
	public ResponseEntity<Object> assignUserToOU(
			@PathVariable(value = "userId") String userId, 
			@PathVariable(value = "ouId") String ouId) {
		String callerId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to assign user {} to OU {}", callerId, userId, ouId);
		Optional<UserDto> optUser = userService.getUser(userId);
		if (optUser.isEmpty()) {
			String notFoundUser = String.format("User %s can't be assigned : user not found - %s", userId,
					HttpStatus.NOT_FOUND.value());
			log.warn(notFoundUser);
			return new ResponseEntity<>(notFoundUser, HttpStatus.NOT_FOUND);
		}

		if (!organizationUnitService.isPresent(ouId)) {
			String notFoundOu = String.format("Organization Unit %s can't be targeted : not found - %s", ouId,
					HttpStatus.NOT_FOUND.value());
			log.warn(notFoundOu);
			return new ResponseEntity<>(notFoundOu, HttpStatus.NOT_FOUND);
		}

		UserDto user = optUser.get();
		OrganizationUnitDto ou = organizationUnitService.findById(ouId).get();
		user.setOrganizationUnit(ou);
		try {
			user = userService.updateUser(user);
		} catch (NotFoundException e) {
			String error = String.format("Error when searching user %s", userId);
			log.warn(error, e);
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
		}

		log.info("{} affected user {} to ou {} - {} ", callerId, userId, ouId, HttpStatus.OK.value());
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	/**
	 * This method is used to delete an user
	 * 
	 * @param request
	 */
	@Operation(summary = "Delete User")
	@DeleteMapping(Constants.API_USER_ID)
	public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") String id) {
		String userId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to delete user {}", userId, id);

		if (!userService.userIsPresent(id)) {
			String noFoundUser = String.format("User %s can't be deleted : not found", id);
			log.warn(noFoundUser);
			return new ResponseEntity<>(noFoundUser, HttpStatus.NOT_FOUND);
		}
		messageService.deleteMessageByUserId(id);
		preferenceService.setPreferences(Collections.emptyList(), id);

		HttpStatus response = userService.delete(id);
		log.info("{} : DELETE User {} resulting in {}", userId, id, response.value());
		return new ResponseEntity<>(response);
	}
}
