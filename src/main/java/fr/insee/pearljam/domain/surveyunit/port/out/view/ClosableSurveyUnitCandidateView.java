package fr.insee.pearljam.domain.surveyunit.port.out.view;

import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.model.StateType;

public interface ClosableSurveyUnitCandidateView {

    String getId();

    StateType getCurrentStateType();

    ContactOutcomeType getContactOutcomeType();
}
