package fr.insee.pearljam.api.controller;

import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.dto.organizationunit.OrganizationUnitDto;
import fr.insee.pearljam.api.dto.user.UserDto;
import fr.insee.pearljam.api.exception.BadRequestException;
import fr.insee.pearljam.api.exception.ConflictException;
import fr.insee.pearljam.api.exception.NoOrganizationUnitException;
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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<UserDto> getUser() {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		Optional<UserDto> user = userService.getUser(currentUserId);
		if (user.isEmpty()) {
			log.info("GET User resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("GET User resulting in 200");
		return new ResponseEntity<>(user.get(), HttpStatus.OK);
	}

	/**
	 * Retrieve a user by its id
	 * @param userId user to retrieve
	 * @return the user corresponding to the id
	 */
	@Operation(summary = "Get User by id")
	@GetMapping(Constants.API_USER_ID)
	public ResponseEntity<UserDto> getUserById(
						@PathVariable(value = "id") String userId) {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		log.info("{} try to GET user with id : {}", currentUserId, userId);
		Optional<UserDto> user = userService.getUser(userId);
		if (user.isEmpty()) {
			log.info("GET User resulting in 404");
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		log.info("GET User resulting in 200");
		return new ResponseEntity<>(user.get(), HttpStatus.OK);
	}

	/**
	 * Create a user
	 * @param user user to created
	 * @return the user created
	 * @throws BadRequestException This is thrown when errors occurred during dto validation
	 */
	@Operation(summary = "Create User")
	@PostMapping(Constants.API_USER)
	@ResponseStatus(value = HttpStatus.CREATED)
	public UserDto createUser(
						@RequestBody @NotNull @Valid UserDto user) throws ConflictException, BadRequestException, NotFoundException {
		String currentUserId = authenticatedUserService.getCurrentUserId();
		log.info("{} tries to create a new user", currentUserId);
		String ouId = user.getOrganizationUnit().getId();
		if (!organizationUnitService.isPresent(ouId)) {
			log.info("Invalid organizational unit {}", ouId);
			throw new BadRequestException("No organizational unit found with this id");
		}

		String userId = user.getId();
		if (userService.userIsPresent(userId)) {
			throw new ConflictException("User already exists", String.format("User %s can't be created : he already exists", userId));
		}

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

		String ouId = user.getOrganizationUnit().getId();
		if (!organizationUnitService.isPresent(ouId)) {
			throw new NotFoundException("No organizational unit found with this id", String.format("Invalid organizational unit %s", ouId));
		}

		if (!userService.userIsPresent(userId)) {
			throw new NotFoundException("No user found with this id", String.format("User %s can't be updated : not found", userId));
		}

		if (!userId.equals(user.getId())) {
			throw new ConflictException("Path id and user id are different", String.format("User %s can't be updated : provided Id [%s] is different", userId, user.getId()));
		}

		UserDto updatedUser = userService.updateUser(user);
		log.info("{} updated user {} - {} ", userId, userId, HttpStatus.OK.value());
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
		Optional<UserDto> optUser = userService.getUser(userId);
		if (optUser.isEmpty()) {
			throw new NotFoundException("User not found");
		}

		Optional<OrganizationUnitDto> ouOptional = organizationUnitService.findById(ouId);
		if (ouOptional.isEmpty()) {
			throw new NotFoundException("Organization unit not found");
		}

		UserDto user = optUser.get();
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
