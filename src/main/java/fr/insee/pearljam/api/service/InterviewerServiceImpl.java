package fr.insee.pearljam.api.service;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.configuration.ApplicationProperties;
import fr.insee.pearljam.api.repository.InterviewerRepository;

/**
 * Implementation of the Service for the Interviewer entity
 * @author scorcaud
 *
 */
@Service
public class InterviewerServiceImpl implements InterviewerService {
	
	@Autowired
	InterviewerRepository interviewerRepository;
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	
	/**
	 * This method check if the Interviewer exist or not in database
	 * @param userId
	 * @return boolean
	 */
	@Override
	public boolean existInterviewer(String userId) {
		return "GUEST".equals(userId) || interviewerRepository.findByIdIgnoreCase(userId).isPresent();
	}
	
	/**
	 * This method retrieve retrieve the UserId passed in the HttpServletRequest. 
	 * Three possible cases which depends of the authentication chosen.
	 * @param HttpServletRequest
	 * @return String of UserId
	 */
	public String getUserId(HttpServletRequest request) {
		String userId = null;
		switch (applicationProperties.getMode()) {
		case Basic:
			Object basic = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (basic instanceof UserDetails) {
				userId = ((UserDetails)basic).getUsername();
			} else {
				userId = basic.toString();
			}
			break;
		case Keycloak:
			KeycloakAuthenticationToken keycloak = (KeycloakAuthenticationToken) request.getUserPrincipal();
			userId = keycloak.getPrincipal().toString();
			break;
		default:
			userId = "GUEST";
			break;
		}
		
		return userId;
	}

}
