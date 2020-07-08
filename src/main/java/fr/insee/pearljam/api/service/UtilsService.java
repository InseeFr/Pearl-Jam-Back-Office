package fr.insee.pearljam.api.service;

import javax.servlet.http.HttpServletRequest;

public interface UtilsService {
	
	/**
	 * This method retrieve the UserId passed in the HttpServletRequest
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	String getUserId(HttpServletRequest request);
	
	/**
	 * This method check if the current User exist or not in database
	 * @param userId
	 * @return boolean
	 */
	boolean existUser(String userId, String service);
}
