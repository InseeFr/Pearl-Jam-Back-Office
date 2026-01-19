package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.exception.*;
import fr.insee.pearljam.api.service.MessageService;
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.api.service.PreferenceService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.domain.security.port.userside.AuthenticatedUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "03. Users", description = "Endpoints for users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {

	private final UserService userService;
	private final MessageService messageService;
	private final OrganizationUnitService organizationUnitService;
	private final PreferenceService preferenceService;
	private final AuthenticatedUserService authenticatedUserService;

	/**
	 * @return the current authenticated user
	 */
	@Operation(summary = "Get current user")
	@GetMapping(Constants.API_USER)
	public UserDto getCurrentUser() throws NotFoundException {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		return userService.getUser(currentUserId);
	}

	/**
	 * Retrieve a user by its id
	 * @param userId user to retrieve
	 * @return the user corresponding to the id
	 */
	@Operation(summary = "Get User by id")
	@GetMapping(Constants.API_USER_ID)
	public UserDto getUserById(
						@PathVariable(value = "id") String userId) throws NotFoundException {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to GET user with id : {}", currentUserId, userId);
		return userService.getUser(userId);
	}

	/**
	 * Create a user
	 * @param user user to created
	 * @return the user created
	 * @throws NotFoundException This is thrown when errors occurred during dto validation
	 * @throws UserAlreadyExistsException This is thrown when errors occurred during dto validation
	 */
	@Operation(summary = "Create User")
	@PostMapping(Constants.API_USER)
	@ResponseStatus(value = HttpStatus.CREATED)
	public UserDto createUser(
						@RequestBody @NotNull @Valid UserDto user) throws NotFoundException, UserAlreadyExistsException {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		log.info("{} tries to create a new user", currentUserId);

		UserDto createdUser = userService.createUser(user);
		log.info("User {} created", user.getId());

		return createdUser;
	}

	/**
	 * Update a user
	 * @param userId user id to update
	 * @param user user to update
	 * @return the updated user
	 */
	@Operation(summary = "Update User")
	@PutMapping(Constants.API_USER_ID)
	public UserDto updateUser(
			@PathVariable(value = "id") String userId,
			@RequestBody @Valid @NotNull UserDto user) throws ConflictException, NotFoundException {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		log.info("{} tries to update user {}", currentUserId, userId);

		if (!userId.equals(user.getId())) {
			throw new ConflictException("Path id and user id are different", String.format("User %s can't be updated : provided Id [%s] is different", userId, user.getId()));
		}

		UserDto updatedUser = userService.updateUser(user);
		log.info("{} updated user {}", userId, userId);
		return updatedUser;
	}

	/**
	 * This method assign a user to target Organization Unit
	 * @param userId user to assign to the organization
	 * @param ouId organization unit id
	 * @return the updated user
	 */
	@Operation(summary = "Assign User to Organization Unit")
	@PutMapping(Constants.API_USER_ID_ORGANIZATION_ID_OUID)
	public UserDto assignUserToOU(
			@PathVariable(value = "userId") String userId, 
			@PathVariable(value = "ouId") String ouId) throws NotFoundException {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		log.info("{} tries to assign user {} to OU {}", currentUserId, userId, ouId);
		UserDto user = userService.getUser(userId);

		Optional<OrganizationUnitDto> ouOptional = organizationUnitService.findById(ouId);
		if (ouOptional.isEmpty()) {
			throw new NotFoundException("Organization unit not found");
		}

		OrganizationUnitDto ou = ouOptional.get();
		user.setOrganizationUnit(ou);
		user = userService.updateUser(user);

		log.info("{} affected user {} to ou {}", currentUserId, userId, ouId);
		return user;
	}

	/**
	 * Delete a user
	 * @param userId user id to delete
	 */
	@Operation(summary = "Delete User")
	@DeleteMapping(Constants.API_USER_ID)
	public void deleteUser(@PathVariable(value = "id") String userId) throws NotFoundException {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		log.info("{} tries to delete user {}", currentUserId, userId);
		messageService.deleteMessageByUserId(userId);
		preferenceService.setPreferences(Collections.emptyList(), userId);
		userService.delete(userId);
		log.info("{} deleted user {}", currentUserId, userId);
	}
}
