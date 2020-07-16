package fr.insee.pearljam.api.service;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.insee.pearljam.api.domain.User;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import fr.insee.pearljam.api.configuration.ApplicationProperties;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;


@Service
public class UtilsServiceImpl implements UtilsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CampaignServiceImpl.class);
	
	@Autowired
	ApplicationProperties applicationProperties;
	
	@Autowired
	InterviewerRepository interviewerRepository;
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	OrganizationUnitRepository organizationUnitRepository;

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
	
	/**
	 * This method check if the User connected exist or not in database
	 * @param userId
	 * @return boolean
	 */
	@Override
	public boolean existUser(String userId, String service) {
		if(service.equals("interviewer")) {
			return "GUEST".equals(userId) || interviewerRepository.findByIdIgnoreCase(userId).isPresent();
		} else if(service.equals("user")) {
			return "GUEST".equals(userId) || userRepository.findByIdIgnoreCase(userId).isPresent();
		} else {
			LOGGER.info("Choose a correct service");
			return false;
		}

	}
  
  /**
	 * This method retreives the organizationUnit of the user as well as all of its children units as a list of String
	 * @param userId
	 * @return List<String> 
	 */
	public List<String> getRelatedOrganizationUnits(String userId){
    List<String> l = new ArrayList<>();
    Optional<User> user = userRepository.findByIdIgnoreCase(userId);

    if("GUEST".equals(userId)){
      l.add("GUEST");
    }
    else if(user.isPresent()){
      l.add(user.get().organizationUnit.id);
      List<String> organizationUnitIds = new ArrayList<>();
      organizationUnitIds = organizationUnitRepository.findChildren(user.get().organizationUnit.id);
      l.addAll(organizationUnitIds);
      for(String idOrg: organizationUnitIds){
        l.addAll(organizationUnitRepository.findChildren(idOrg));
      }
    }

    return l;
  }
}
