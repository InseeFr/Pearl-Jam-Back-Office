package fr.insee.pearljam.api.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.pearljam.api.domain.SurveyUnit;
import fr.insee.pearljam.api.dto.user.UserDto;
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

	/**
	 * This method is using to get the list of Campaigns for current user
	 * 
	 * @return List of {@link SurveyUnit} if exist, {@link HttpStatus} NOT_FOUND, or
	 *         {@link HttpStatus} FORBIDDEN
	 */
	@ApiOperation(value = "Get User")
	@GetMapping(path = "/user")
	public ResponseEntity<Object> getUser(HttpServletRequest request) {
		String userId = utilsService.getUserId(request);
		if (StringUtils.isBlank(userId) || !utilsService.existUser(userId, "user")) {
			LOGGER.info("GET User resulting in 403" );
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		} else {
			UserDto user = userService.getUser(userId);
			if(user!=null) {
				LOGGER.info("GET User resulting in 200" );
				return new ResponseEntity<>(user, HttpStatus.OK);
			}else {
				LOGGER.info("GET User resulting in 404" );
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		}
	}
}
