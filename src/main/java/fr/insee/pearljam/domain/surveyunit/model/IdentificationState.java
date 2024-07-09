package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.domain.surveyunit.model.question.CategoryQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.IdentificationQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.SituationQuestionValue;

public enum IdentificationState {
    MISSING, FINISHED, ONGOING;

    /**
     * Retrieve the identification state from the identification
     * @param identification Identification form which the state is computed
     * @return the identification state
     */
    public static IdentificationState getState(Identification identification) {
        if (identification == null || identification.identification() == null) {
            return IdentificationState.MISSING;
        }

        IdentificationQuestionValue identificationQuestionValue = identification.identification();
        if(identificationQuestionValue == IdentificationQuestionValue.DESTROY ||
                identificationQuestionValue == IdentificationQuestionValue.UNIDENTIFIED) {
            return IdentificationState.FINISHED;
        }

        if(identification.access() == null) {
            return IdentificationState.ONGOING;
        }

        if(identification.occupant() != null) {
            return IdentificationState.FINISHED;
        }

        SituationQuestionValue situationQuestionValue = identification.situation();
        if(situationQuestionValue == SituationQuestionValue.ABSORBED ||
                situationQuestionValue == SituationQuestionValue.NOORDINARY) {
            return IdentificationState.FINISHED;
        }

        CategoryQuestionValue categoryQuestionValue = identification.category();
        if(categoryQuestionValue == CategoryQuestionValue.VACANT ||
                categoryQuestionValue == CategoryQuestionValue.SECONDARY) {
            return IdentificationState.FINISHED;
        }
        return IdentificationState.ONGOING;
    }
}
