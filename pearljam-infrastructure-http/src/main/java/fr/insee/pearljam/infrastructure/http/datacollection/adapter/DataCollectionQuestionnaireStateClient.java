package fr.insee.pearljam.infrastructure.http.datacollection.adapter;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import fr.insee.pearljam.domain.surveyunit.port.out.QuestionnaireStateClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataCollectionQuestionnaireStateClient implements QuestionnaireStateClient {

    @Qualifier("dataCollectionRestClient")
    private final RestClient.Builder dataCollectionRestClient;

    @Override
    public ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(
            Set<String> ids) {
        return dataCollectionRestClient
                .build()
                .post()
                .uri(Constants.API_QUEEN_INTERROGATIONS_STATEDATA)
                .body(ids)
                .retrieve()
                .toEntity(InterrogationOkNokDto.class);
    }


}
