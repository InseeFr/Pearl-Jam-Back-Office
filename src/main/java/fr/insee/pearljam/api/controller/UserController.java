package fr.insee.pearljam.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import fr.insee.pearljam.api.service.OrganizationUnitService;
import fr.insee.pearljam.api.service.UserService;
import fr.insee.pearljam.api.service.UtilsService;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(path = "/api")
public class UserController {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UtilsService utilsService;

	@Autowired
	UserService userService;
	
	@Autowired
	OrganizationUnitService organizationUnitService;

	/**
	 * This method returns the current USER
	 * 
	 * @param request
	 * @return List of {@link UserDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get User")
	@GetMapping(path = "/user")
	public ResponseEntity<UserDto> getUser(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId)) {
			LOGGER.info("GET User resulting in 403");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			UserDto user;
			try {
				user = userService.getUser(userId);
			}
			catch(NotFoundException e) {
				LOGGER.info("GET User resulting in 404");
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET User resulting in 200");
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
	}
	
	/**
	 * This method returns user matching with the `id` param
	 * 
	 * @param request
	 * @return List of {@link UserDto} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get User by id")
	@GetMapping(path = "/user/{id}")
	public ResponseEntity<UserDto> getUserById(HttpServletRequest request, @PathVariable(value = "id") String id) {
		String userId = utilsService.getUserId(request);
		LOGGER.info("{} try to GET user with id : {}", userId, id);
		if (StringUtils.isBlank(userId)) {
			LOGGER.info("GET User {} resulting in 403", id);
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			UserDto user;
			try {
				user = userService.getUser(id);
			} catch (NotFoundException e) {
				LOGGER.info("GET User {} resulting in 404", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			LOGGER.info("GET User resulting in 200");
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
	}

	/**
	 * This method creates a user
	 * 
	 * @param request
	 */
	@ApiOperation(value = "Create User")
	@PostMapping(path = "/user")
	public ResponseEntity<Object> createUser(HttpServletRequest request, @RequestBody UserDto user ) {
		String callerId = utilsService.getUserId(request);
		LOGGER.info("{} try to create a new user", callerId);
		if (!userService.checkValidity(user)) {
			String invalidUserInfo = String.format("Invalid user : %s", user.toString());
			LOGGER.info(invalidUserInfo);
			return new ResponseEntity<>(invalidUserInfo, HttpStatus.BAD_REQUEST);
		}

		if (userService.userIsPresent(user.getId())) {
			String alreadyPresentUserInfo = String.format("User %s can't be created : already present",
					user.toString());
			LOGGER.warn(alreadyPresentUserInfo);
			return new ResponseEntity<>(alreadyPresentUserInfo, HttpStatus.CONFLICT);
		}

		UserDto createdUser = null;
		try {
			createdUser = userService.createUser(user);
			LOGGER.info("User {} created", user.getId());
		} catch (Exception e) {
			String unexpectedError = String.format("Exception when creating User %s" , user.getId());
			LOGGER.warn(unexpectedError, e);
			return new ResponseEntity<>(unexpectedError, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	/**
	 * This method updates a user
	 * 
	 * @param request
	 */
	@ApiOperation(value = "Update User")
	@PutMapping(path = "/user/{id}")
	public ResponseEntity<Object> updateUser(HttpServletRequest request, @PathVariable(value = "id") String id,
			@RequestBody UserDto user) {
		String callerId = utilsService.getUserId(request);
		LOGGER.info("{} try to update user {}", callerId, id);

		if (!userService.checkValidity(user)) {
			String invalidUserInfo = String.format("Invalid user : %s", user.toString());
			LOGGER.info(invalidUserInfo);
			return new ResponseEntity<>(invalidUserInfo, HttpStatus.BAD_REQUEST);
		}

		if (!userService.userIsPresent(id)) {
			String noFoundUser = String.format("User %s can't be updated : not found", id);
			LOGGER.warn(noFoundUser);
			return new ResponseEntity<>(noFoundUser, HttpStatus.NOT_FOUND);
		}

		if (!id.equals(user.getId())) {
			String differentUser = String.format("User %s can't be updated : provided Id [%s] is different", id,
					user.getId());
			LOGGER.warn(differentUser);
			return new ResponseEntity<Object>(differentUser, HttpStatus.CONFLICT);
		}

		UserDto updatedUser = userService.updateUser(user);
		LOGGER.info("{} updated user {} - {} ",callerId, id, HttpStatus.OK.value());
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	/**
	 * This method assign a user to target Organization Unit
	 * 
	 * @param request
	 */
	@ApiOperation(value = "Assign User to Organization Unit")
	@PutMapping(path = "/user/{userId}/organization-unit/{ouId}")
	public ResponseEntity<Object> assignUserToOU(HttpServletRequest request,
			@PathVariable(value = "userId") String userId, @PathVariable(value = "ouId") String ouId) {
		String callerId = utilsService.getUserId(request);
		LOGGER.info("{} try to assign user {} to OU {}", callerId, userId, ouId);
		if (!userService.userIsPresent(userId)) {
			String notFoundUser = String.format("User %s can't be assigned : user not found - {}", userId,
					HttpStatus.NOT_FOUND.value());
			LOGGER.warn(notFoundUser);
			return new ResponseEntity<>(notFoundUser, HttpStatus.NOT_FOUND);
		}

		if (!organizationUnitService.isPresent(ouId)) {
			String notFoundOu = String.format("Organization Unit %s can't be targeted : not found - {}", ouId,
					HttpStatus.NOT_FOUND.value());
			LOGGER.warn(notFoundOu);
			return new ResponseEntity<>(notFoundOu, HttpStatus.NOT_FOUND);
		}

		UserDto user = null;
		try {
			user = userService.getUser(userId);
		} catch (NotFoundException e) {
			LOGGER.warn("Error when searching user {}", userId, e);
		}
		OrganizationUnitDto ou = organizationUnitService.findById(ouId).get();
		user.setOrganizationUnit(ou);
		userService.updateUser(user);

		UserDto updatedUser = userService.updateUser(user);
		LOGGER.info("{} affected user {} to ou {} - {} ", callerId, userId, ouId, HttpStatus.OK.value());
		return new ResponseEntity<>(updatedUser, HttpStatus.OK);
	}

	/**
	 * This method is used to delete an user
	 * 
	 * @param request
	 */
	@ApiOperation(value = "Delete User")
	@DeleteMapping(path = "/user/{id}")
	public ResponseEntity<Object> deleteUser(HttpServletRequest request, @PathVariable(value = "id") String id) {
		String callerId = utilsService.getUserId(request);
		LOGGER.info("{} try to delete user {}", callerId, id);

		if (!userService.userIsPresent(id)) {
			String noFoundUser = String.format("User %s can't be deleted : not found", id);
			LOGGER.warn(noFoundUser);
			return new ResponseEntity<>(noFoundUser, HttpStatus.NOT_FOUND);
		}

		HttpStatus response = userService.delete(id);
		LOGGER.info("{} : DELETE User {} resulting in {}",callerId, id, response.value());
		return new ResponseEntity<>(response);
	}
}
