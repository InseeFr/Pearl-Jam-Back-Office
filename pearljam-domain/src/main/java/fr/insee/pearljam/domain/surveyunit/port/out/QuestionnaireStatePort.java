package fr.insee.pearljam.domain.surveyunit.port.out;

import java.util.Map;
import java.util.Set;

public interface QuestionnaireStatePort {
    Map<String, String> getStates(Set<String> ids);
}
