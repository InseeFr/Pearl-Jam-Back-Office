package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.domain.surveyunit.model.QuestionnaireState;

import java.util.Map;
import java.util.Set;

public interface QuestionnaireStatePort {
    Map<String, QuestionnaireState> getStates(Set<String> ids);
}
