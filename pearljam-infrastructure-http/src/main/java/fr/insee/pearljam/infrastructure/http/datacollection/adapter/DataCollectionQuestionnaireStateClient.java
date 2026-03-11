package fr.insee.pearljam.infrastructure.http.datacollection.adapter;

import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.infrastructure.http.datacollection.config.DataCollectionHttpProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataCollectionQuestionnaireStateClient implements QuestionnaireStateClient {

	private final DataCollectionHttpProperties dataCollectionHttpProperties;
	private final RestTemplate restTemplate;

	@Override
	public ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request,
			Set<String> ids) {
		final String dataCollectionUri = String.join("", dataCollectionHttpProperties.datacollectionUrl(),
				Constants.API_QUEEN_INTERROGATIONS_STATEDATA);

		String authTokenHeader = request.getHeader(Constants.AUTHORIZATION);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(Constants.AUTHORIZATION, authTokenHeader);
		return restTemplate.exchange(dataCollectionUri, HttpMethod.POST, new HttpEntity<>(ids, headers),
				InterrogationOkNokDto.class);
	}
}
