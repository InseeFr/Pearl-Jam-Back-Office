package fr.insee.pearljam.domain.surveyunit.service;


import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SurveyUnitToClosePolicy {

    public boolean isClosable(
            ClosableSurveyUnitCandidateView candidate,
            String questionnaireState) {

        if (candidate.getCurrentStateType() == null) {
            return false;
        }
        return isNeverTransmitted(candidate)
               || isInaWithoutQuestionnaire(candidate, questionnaireState);
    }

    private boolean isNeverTransmitted(ClosableSurveyUnitCandidateView c) {
        return c.getCurrentStateType() != null
               && !Set.of(StateType.TBR, StateType.FIN, StateType.CLO)
                .contains(c.getCurrentStateType());
    }

    private boolean isInaWithoutQuestionnaire(
            ClosableSurveyUnitCandidateView c,
            String questionnaireState) {

        return c.getContactOutcomeType() == ContactOutcomeType.INA
               && (questionnaireState == null || "UNAVAILABLE".equals(questionnaireState))
               && c.getCurrentStateType() != StateType.CLO;
    }
}
