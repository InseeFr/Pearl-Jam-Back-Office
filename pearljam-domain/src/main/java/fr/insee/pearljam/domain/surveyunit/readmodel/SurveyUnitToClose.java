package fr.insee.pearljam.domain.surveyunit.readmodel;

import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SurveyUnitToClose {

    private String campaignLabel;
    private String surveyUnitId;
    private String interviewerLabel;
    private Integer ssech;
    private String identificationState;
    private ContactOutcomeType contactOutcome;
    private String questionnaireState;
    private ClosingCauseType closingCause;

    public static SurveyUnitToClose from(
            ClosableSurveyUnitCandidateView candidate,
            ClosableSurveyUnitView projection,
            String questionnaireState) {

        String interviewerFirstName = projection.getInterviewerFirstName();
        String interviewerLastName = projection.getInterviewerLastName();
        String interviewerLabel = (interviewerFirstName != null && interviewerLastName != null) ?
                (interviewerFirstName + " " + interviewerLastName).trim() :
                null;

        return new SurveyUnitToClose(
                projection.getCampaignLabel(),
                candidate.getId(),
                interviewerLabel,
                projection.getSsech(),
                computeIdentificationState(projection),
                candidate.getContactOutcomeType(),
                questionnaireState,
                SurveyUnitMappers.computeClosingCause(projection.getClosingCauseType(), candidate.getCurrentStateType())
        );
    }

    private static String computeIdentificationState(ClosableSurveyUnitView projection) {
        return IdentificationState.getState(
                toModelIdentification(projection),
                projection.getCampaignIdentificationConfiguration()
        ).name();
    }

    private static Identification toModelIdentification(ClosableSurveyUnitView projection) {
        return Identification.builder()
                .identificationType(projection.getIdentificationType())
                .identification(projection.getIdentification())
                .access(projection.getAccess())
                .situation(projection.getSituation())
                .category(projection.getCategory())
                .occupant(projection.getOccupant())
                .individualStatus(projection.getIndividualStatus())
                .interviewerCanProcess(projection.getInterviewerCanProcess())
                .numberOfRespondents(projection.getNumberOfRespondents())
                .presentInPreviousHome(projection.getPresentInPreviousHome())
                .householdComposition(projection.getHouseholdComposition())
                .build();
    }
}
