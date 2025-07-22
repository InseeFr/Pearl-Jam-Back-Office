package fr.insee.pearljam.domain.surveyunit.model;

import fr.insee.pearljam.api.domain.IdentificationConfiguration;
import fr.insee.pearljam.domain.surveyunit.model.question.*;

import java.util.EnumSet;

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

            case INDF2F, INDF2FNOR -> individuF2F(identification);

            case INDTEL, INDTELNOR -> individuTel(identification);

            // additional rules coming soon
            case HOUSETEL, HOUSETELWSR, SRCVREINT -> IdentificationState.MISSING;

        };

    }

    private static IdentificationState individuTel(Identification identification) {
        IndividualStatusQuestionValue status = identification.individualStatus();
        if (status == null) return IdentificationState.ONGOING;
        if (EnumSet.of(IndividualStatusQuestionValue.DCD, IndividualStatusQuestionValue.NOIDENT, IndividualStatusQuestionValue.NOFIELD)
            .contains(status))
            return IdentificationState.FINISHED;

        SituationQuestionValue situation = identification.situation();
        if (EnumSet.of(IndividualStatusQuestionValue.SAME_ADDRESS, IndividualStatusQuestionValue.OTHER_ADDRESS).contains(status))
            return (situation == null) ? IdentificationState.ONGOING : IdentificationState.FINISHED;

        return (situation != null) ? IdentificationState.ONGOING : null;
    }


    private static IdentificationState individuF2F(Identification identification) {
        IndividualStatusQuestionValue status = identification.individualStatus();
        if (status == null) return IdentificationState.ONGOING;
        if (EnumSet.of(IndividualStatusQuestionValue.DCD, IndividualStatusQuestionValue.NOIDENT, IndividualStatusQuestionValue.NOFIELD)
            .contains(status))
            return IdentificationState.FINISHED;

        SituationQuestionValue situation = identification.situation();
        if (status == IndividualStatusQuestionValue.SAME_ADDRESS)
            return (situation == null) ? IdentificationState.ONGOING : IdentificationState.FINISHED;

        InterviewerCanProcessQuestionValue interviewer = identification.interviewerCanProcess();
        if (status == IndividualStatusQuestionValue.OTHER_ADDRESS) {
            if (interviewer == null) return IdentificationState.ONGOING;
            if (interviewer == InterviewerCanProcessQuestionValue.NO) return IdentificationState.FINISHED;
        }

        if (interviewer == InterviewerCanProcessQuestionValue.YES && situation == null) return IdentificationState.ONGOING;
        if (EnumSet.of(SituationQuestionValue.NOORDINARY, SituationQuestionValue.ABSORBED).contains(situation))
            return IdentificationState.FINISHED;

        if (interviewer == InterviewerCanProcessQuestionValue.NO) return IdentificationState.ONGOING;

        return null;
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

        if (identification.access() == AccessQuestionValue.NACC) {
            return IdentificationState.FINISHED;
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
