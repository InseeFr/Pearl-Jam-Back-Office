package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.contracts.surveyunit.dto.surveyunit.InterrogationOkNokDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface QuestionnaireStateClient {

    ResponseEntity<InterrogationOkNokDto> getQuestionnairesStateFromDataCollection(HttpServletRequest request, Set<String> ids);
}
