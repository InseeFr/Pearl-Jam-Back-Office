package fr.insee.pearljam.api.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.configuration.ApplicationProperties;
import fr.insee.pearljam.api.repository.InterviewerRepository;

@Service
public class InterviewerServiceImpl implements InterviewerService {
	
	@Autowired
	InterviewerRepository interviewerRepository;
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	private static final String IDINTERVIEWER = "idInterviewer";
	
	@Override
	public boolean existInterviewer(String userId) {
		return interviewerRepository.findById(userId).isPresent() || "GUEST".equals(userId);
	}
	
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
			IDToken token = keycloak.getAccount().getKeycloakSecurityContext().getIdToken();
			Map<String, Object> otherClaims = token.getOtherClaims();
			if (otherClaims.containsKey(IDINTERVIEWER)) {
				userId = String.valueOf(otherClaims.get(IDINTERVIEWER));
			}
			break;
		default:
			userId = "GUEST";
			break;
		}
		
		return userId;
	}

}
