package fr.insee.pearljam.api.service;

import java.util.List;

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

  /**
	 * This method retreives the organizationUnit of the user as well as all of its children units as a list of String
	 * @param userId
	 * @return List<String> 
	 */
	List<String> getRelatedOrganizationUnits(String userId);
}
