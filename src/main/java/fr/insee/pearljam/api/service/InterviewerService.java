package fr.insee.pearljam.api.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Service for the Interviewer entity
 * @author scorcaud
 *
 */
public interface InterviewerService {

	/**
	 * This method check if the Interviewer exist or not in database
	 * @param userId
	 * @return boolean
	 */
	boolean existInterviewer(String userId);

	/**
	 * This method retrieve the UserId passed in the HttpServletRequest
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	String getUserId(HttpServletRequest request);

}
