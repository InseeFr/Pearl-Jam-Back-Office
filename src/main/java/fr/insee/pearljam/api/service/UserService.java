package fr.insee.pearljam.api.service;

import fr.insee.pearljam.api.dto.user.UserDto;

/**
 * Service for the Interviewer entity
 * @author scorcaud
 *
 */
public interface UserService {

	UserDto getUser(String userId);

}
