package fr.insee.pearljam.api.surveyunit.response;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(name = "SurveyUnitToClose")
public record SurveyUnitToCloseResponse(
        String campaignLabel,
        String surveyUnitId,
        String interviewerLabel,
        Integer ssech,
        String identificationState,
        ContactOutcomeType contactOutcome,
        String questionnaireState,
        ClosingCauseType closingCause
) {
    public static SurveyUnitToCloseResponse from(
            ClosableSurveyUnitCandidateView candidate,
            ClosableSurveyUnitView projection,
            String questionnaireState
    ) {

        return new SurveyUnitToCloseResponse(
                projection.getCampaignLabel(),
                projection.getId(),
                projection.getInterviewerFirstName() + " " + projection.getInterviewerLastName(),
                projection.getSsech(),
                candidate.getCurrentStateType() != null
                        ? candidate.getCurrentStateType().name()
                        : null,
                candidate.getContactOutcomeType(),
                questionnaireState,
                projection.getClosingCauseType()
        );
    }
}