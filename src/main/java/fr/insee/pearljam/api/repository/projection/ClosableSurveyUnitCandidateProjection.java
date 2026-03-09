package fr.insee.pearljam.api.repository.projection;

import fr.insee.pearljam.domain.contactoutcome.model.ContactOutcomeType;
import fr.insee.pearljam.domain.state.model.StateType;

public interface ClosableSurveyUnitCandidateProjection {

    String getId();

    StateType getCurrentStateType();

    ContactOutcomeType getContactOutcomeType();
}
