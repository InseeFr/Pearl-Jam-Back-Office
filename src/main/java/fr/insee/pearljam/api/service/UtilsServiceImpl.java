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
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.repository.CampaignRepository;
import fr.insee.pearljam.api.repository.InterviewerRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;

@Service
public class UtilsServiceImpl implements UtilsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UtilsServiceImpl.class);

	@Autowired
	ApplicationProperties applicationProperties;

	@Autowired
	InterviewerRepository interviewerRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	OrganizationUnitRepository organizationUnitRepository;

	@Autowired
	UserService userService;

	public String getUserId(HttpServletRequest request) {
		String userId = null;
		switch (applicationProperties.getMode()) {
		case Basic:
			Object basic = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (basic instanceof UserDetails) {
				userId = ((UserDetails) basic).getUsername();
			} else {
				userId = basic.toString();
			}
			break;
		case Keycloak:
			KeycloakAuthenticationToken keycloak = (KeycloakAuthenticationToken) request.getUserPrincipal();
			userId = keycloak.getPrincipal().toString();
			break;
		default:
			userId = Constants.GUEST;
			break;
		}
		return userId;
	}

	public boolean existUser(String userId, String service) {
		if (service.equals(Constants.INTERVIEWER)) {
			return Constants.GUEST.equals(userId) || interviewerRepository.findByIdIgnoreCase(userId).isPresent();
		} else if (service.equals(Constants.USER)) {
			return Constants.GUEST.equals(userId) || userRepository.findByIdIgnoreCase(userId).isPresent();
		} else {
			LOGGER.info("Choose a correct service");
			return false;
		}

	}

	public List<String> getRelatedOrganizationUnits(String userId) {
		List<String> l = new ArrayList<>();
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);

		if ("GUEST".equals(userId)) {
			l.add("GUEST");
		} else if (user.isPresent()) {
			l.add(user.get().organizationUnit.id);
			List<String> organizationUnitIds = organizationUnitRepository.findChildrenId(user.get().organizationUnit.id);
			l.addAll(organizationUnitIds);
			for (String idOrg : organizationUnitIds) {
				l.addAll(organizationUnitRepository.findChildrenId(idOrg));
			}
		}

		return l;
	}

	public boolean checkUserCampaignOUConstraints(String userId, String campaignId) {
		if (!userRepository.existsByIdIgnoreCase(userId) && !Constants.GUEST.equals(userId)) {
			LOGGER.error(Constants.ERR_USER_NOT_EXIST, userId);
			return false;
		}
		if (!campaignRepository.existsById(campaignId)) {
			LOGGER.error(Constants.ERR_CAMPAIGN_NOT_EXIST, campaignId);
			return false;
		}
		if (!userService.isUserAssocitedToCampaign(campaignId, userId) && !Constants.GUEST.equals(userId)) {
			LOGGER.error(Constants.ERR_NO_OU_FOR_CAMPAIGN, campaignId, userId);
			return false;
		}
		return true;
	}
}
