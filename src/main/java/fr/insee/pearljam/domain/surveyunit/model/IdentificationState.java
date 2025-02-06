package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.question.CategoryQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.IdentificationQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.SituationQuestionValue;

public enum IdentificationState {
    MISSING, FINISHED, ONGOING;

    /**
     * Retrieve the identification state from the identification
     *
     * @param identification Identification from which the state is computed
     * @param configuration Identification configuration to adapt rules
     * @return the identification state
     */
    public static IdentificationState getState(Identification identification,
                                               IdentificationConfiguration configuration) {
        if (identification == null) {
            return IdentificationState.MISSING;
        }

        return switch (configuration) {
            case NOIDENT -> IdentificationState.MISSING;

            case IASCO, HOUSEF2F -> houseF2F(identification);

            // additional rules coming soon
            case HOUSETEL, HOUSETELWSR, INDF2F, INDF2FNOR, INDTEL, INDTELNOR, SRCVREINT -> IdentificationState.MISSING;

        };


    }

    private static IdentificationState houseF2F(Identification identification) {
        IdentificationQuestionValue identificationQuestionValue = identification.identification();
        if (identificationQuestionValue == null) {
            return IdentificationState.MISSING;
        }

        if (identificationQuestionValue == IdentificationQuestionValue.DESTROY ||
                identificationQuestionValue == IdentificationQuestionValue.UNIDENTIFIED) {
            return IdentificationState.FINISHED;
        }

        if (identification.access() == null) {
            return IdentificationState.ONGOING;
        }

        SituationQuestionValue situationQuestionValue = identification.situation();
        if (situationQuestionValue == null) {
            return IdentificationState.ONGOING;
        }

        if (situationQuestionValue == SituationQuestionValue.ABSORBED ||
                situationQuestionValue == SituationQuestionValue.NOORDINARY) {
            return IdentificationState.FINISHED;
        }

        CategoryQuestionValue categoryQuestionValue = identification.category();
        if (categoryQuestionValue == null) {
            return IdentificationState.ONGOING;
        }

        if (categoryQuestionValue == CategoryQuestionValue.VACANT ||
                categoryQuestionValue == CategoryQuestionValue.SECONDARY) {
            return IdentificationState.FINISHED;
        }

        return identification.occupant() == null ? IdentificationState.ONGOING : IdentificationState.FINISHED;
    }

}
