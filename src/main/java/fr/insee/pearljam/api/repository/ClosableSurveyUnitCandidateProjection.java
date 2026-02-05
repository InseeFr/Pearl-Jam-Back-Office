package fr.insee.pearljam.api.repository;

import fr.insee.pearljam.api.domain.ContactOutcomeType;
import fr.insee.pearljam.api.domain.StateType;

public interface ClosableSurveyUnitCandidateProjection {

    String getId();

    StateType getCurrentStateType();

    ContactOutcomeType getContactOutcomeType();
}
