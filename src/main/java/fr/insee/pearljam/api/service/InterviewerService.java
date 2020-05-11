package fr.insee.pearljam.api.service;

import javax.servlet.http.HttpServletRequest;

public interface InterviewerService {

	boolean existInterviewer(String userId);

	String getUserId(HttpServletRequest request);

}
