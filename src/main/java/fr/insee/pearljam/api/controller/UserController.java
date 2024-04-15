package fr.insee.pearljam.api.controller;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.exception.NotFoundException;
import fr.insee.pearljam.api.service.MessageService;
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.web.authentication.AuthenticationHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Tag(name = "03. Users", description = "Endpoints for users")
@RequestMapping(path = "/api")
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final AuthenticationHelper authHelper;

	private final UserService userService;
	private final MessageService messageService;
	private final OrganizationUnitService organizationUnitService;

	/**
	 * This method returns the current USER
	 * 
	 * @param request
	 * @return List of {@link UserDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get User")
	@GetMapping(path = "/user")
	public ResponseEntity<UserDto> getUser(Authentication auth) {
		String userId = authHelper.getUserId(auth);
		if (StringUtils.isBlank(userId)) {
			log.info("GET User resulting in 403");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			Optional<UserDto> user = userService.getUser(userId);
			if (user.isEmpty()) {
				log.info("GET User resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("GET User resulting in 200");
			return new ResponseEntity<>(user.get(), HttpStatus.OK);
		}

	}

	/**
	 * This method returns user matching with the `id` param
	 * 
	 * @param request
	 * @return List of {@link UserDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@Operation(summary = "Get User by id")
	@GetMapping(path = "/user/{id}")
	public ResponseEntity<UserDto> getUserById(Authentication auth, @PathVariable(value = "id") String id) {
		String userId = authHelper.getUserId(auth);
		log.info("{} try to GET user with id : {}", userId, id);
		if (StringUtils.isBlank(userId)) {
			log.info("GET User {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			Optional<UserDto> user = userService.getUser(id);
			if (user.isEmpty()) {
				log.info("GET User resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.info("GET User resulting in 200");
			return new ResponseEntity<>(user.get(), HttpStatus.OK);
		}
	}

	/**
	 * This method creates a user
	 * 
	 * @param request
	 */
	@Operation(summary = "Create User")
	@PostMapping(path = "/user")
	public ResponseEntity<Object> createUser(Authentication auth, @RequestBody UserDto user) {
		String callerId = authHelper.getUserId(auth);
		log.info("{} try to create a new user", callerId);
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

		UserDto createdUser = null;
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
	@PutMapping(path = "/user/{id}")
	public ResponseEntity<Object> updateUser(Authentication auth, @PathVariable(value = "id") String id,
			@RequestBody UserDto user) {
		String callerId = authHelper.getUserId(auth);
		log.info("{} try to update user {}", callerId, id);

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
		log.info("{} updated user {} - {} ", callerId, id, HttpStatus.OK.value());
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	/**
	 * This method assign a user to target Organization Unit
	 * 
	 * @param request
	 */
	@Operation(summary = "Assign User to Organization Unit")
	@PutMapping(path = "/user/{userId}/organization-unit/{ouId}")
	public ResponseEntity<Object> assignUserToOU(Authentication auth,
			@PathVariable(value = "userId") String userId, @PathVariable(value = "ouId") String ouId) {
		String callerId = authHelper.getUserId(auth);
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
	@DeleteMapping(path = "/user/{id}")
	public ResponseEntity<Object> deleteUser(Authentication auth, @PathVariable(value = "id") String id) {
		String callerId = authHelper.getUserId(auth);
		log.info("{} try to delete user {}", callerId, id);

		if (!userService.userIsPresent(id)) {
			String noFoundUser = String.format("User %s can't be deleted : not found", id);
			log.warn(noFoundUser);
			return new ResponseEntity<>(noFoundUser, HttpStatus.NOT_FOUND);
		}
		messageService.deleteMessageByUserId(id);

		HttpStatus response = userService.delete(id);
		log.info("{} : DELETE User {} resulting in {}", callerId, id, response.value());
		return new ResponseEntity<>(response);
	}
}
