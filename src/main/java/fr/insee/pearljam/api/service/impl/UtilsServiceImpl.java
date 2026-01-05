package fr.insee.pearljam.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.insee.pearljam.api.configuration.properties.ExternalServicesProperties;
import fr.insee.pearljam.api.constants.Constants;
import fr.insee.pearljam.api.domain.User;
import fr.insee.pearljam.api.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.api.repository.OrganizationUnitRepository;
import fr.insee.pearljam.api.repository.UserRepository;
import fr.insee.pearljam.api.service.UtilsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UtilsServiceImpl implements UtilsService {

	private final ExternalServicesProperties externalServicesProperties;
	private final UserRepository userRepository;
	private final OrganizationUnitRepository organizationUnitRepository;
	private final RestTemplate restTemplate;

	public List<String> getRelatedOrganizationUnits(String userId) {
		List<String> l = new ArrayList<>();
		Optional<User> user = userRepository.findByIdIgnoreCase(userId);

		if ("GUEST".equals(userId)) {
			l.add("GUEST");
		} else if (user.isPresent()) {
			l.add(user.get().getOrganizationUnit().getId());
			List<String> organizationUnitIds = organizationUnitRepository
					.findChildrenId(user.get().getOrganizationUnit().getId());
			l.addAll(organizationUnitIds);
			for (String idOrg : organizationUnitIds) {
				l.addAll(organizationUnitRepository.findChildrenId(idOrg));
			}
		}

		return l;
	}

	@Override
	public ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request,
			List<String> ids) {
		final String dataCollectionUri = String.join("", externalServicesProperties.datacollectionUrl(),
				Constants.API_QUEEN_INTERROGATIONS_STATEDATA);

		String authTokenHeader = request.getHeader(Constants.AUTHORIZATION);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(Constants.AUTHORIZATION, authTokenHeader);
		return restTemplate.exchange(dataCollectionUri, HttpMethod.POST, new HttpEntity<>(ids, headers),
				InterrogationOkNokDto.class);
	}
}
