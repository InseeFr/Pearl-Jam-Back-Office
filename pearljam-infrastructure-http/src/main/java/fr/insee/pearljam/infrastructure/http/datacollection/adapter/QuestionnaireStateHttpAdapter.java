package fr.insee.pearljam.infrastructure.http.datacollection.adapter;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStatePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuestionnaireStateHttpAdapter implements QuestionnaireStatePort {

    private final QuestionnaireStateClient client;

    @Override
    public Map<String, String> getStates(Set<String> ids) {

        Map<String, String> result = new HashMap<>();

        try {
            ResponseEntity<InterrogationOkNokDto> response =
                    client.getQuestionnairesStateFromDataCollection(ids);

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                log.error("API error {}", response.getStatusCode());
            }

            InterrogationOkNokDto body = response.getBody();
            if (body == null) {
                throw new IllegalStateException("Empty response");
            }

            body.interrogationOK()
                    .forEach(su -> result.put(su.id(), su.stateData().getState()));

            body.interrogationNOK()
                    .forEach(su -> result.put(su.id(), su.stateData().getState()));

        } catch (Exception e) {
            log.error("Fallback UNAVAILABLE", e);
            ids.forEach(id -> result.put(id, Constants.QUESTIONNAIRE_STATE_UNAVAILABLE));
        }

        return result;
    }
}
