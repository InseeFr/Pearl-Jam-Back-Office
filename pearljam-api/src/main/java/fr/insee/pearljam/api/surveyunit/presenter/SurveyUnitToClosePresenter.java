package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToCloseStatsPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.insee.pearljam.contracts.constants.Constants.QUESTIONNAIRE_STATE_UNAVAILABLE;

@Component
public class SurveyUnitToClosePresenter implements SurveyUnitToCloseStatsPresenter<List<SurveyUnitToCloseResponse>> {

    @Override
    public List<SurveyUnitToCloseResponse> present(
            List<ClosableSurveyUnitView> projections,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates
    ) {

        return projections.stream()
                .map(projection -> {
                    String id = projection.getId();

                    var candidate = candidatesById.get(id);

                    ContactOutcomeType contactOutcome =
                            candidate != null ? candidate.getContactOutcomeType() : null;

                    String interviewerLabel = buildInterviewerLabel(projection);

                    String questionnaireState = questionnaireStates.getOrDefault(
                            id,
                            QUESTIONNAIRE_STATE_UNAVAILABLE
                    );

                    return new SurveyUnitToCloseResponse(
                            projection.getCampaignLabel(),
                            projection.getId(),
                            projection.getDisplayName(),
                            interviewerLabel,
                            projection.getSsech(),
                            computeIdentificationState(projection).name(),
                            contactOutcome,
                            questionnaireState,
                            projection.getClosingCauseType()
                    );
                })
                .toList();
    }

    private String buildInterviewerLabel(ClosableSurveyUnitView projection) {
        String firstName = projection.getInterviewerFirstName();
        String lastName = projection.getInterviewerLastName();

        if (firstName == null && lastName == null) return null;

        return Stream.of(firstName, lastName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    private static IdentificationState computeIdentificationState(ClosableSurveyUnitView p) {
        return IdentificationState.getState(
                toModelIdentification(p),
                p.getCampaignIdentificationConfiguration()
        );
    }
    private static Identification toModelIdentification(ClosableSurveyUnitView p) {
        boolean allNull =
                p.getIdentification() == null
                && p.getAccess() == null
                && p.getSituation() == null
                && p.getCategory() == null
                && p.getOccupant() == null
                && p.getIndividualStatus() == null
                && p.getInterviewerCanProcess() == null
                && p.getNumberOfRespondents() == null
                && p.getPresentInPreviousHome() == null
                && p.getHouseholdComposition() == null
                && p.getIdentificationType() == null;

        if (allNull) return null;

        return Identification.builder()
                .identificationType(p.getIdentificationType())
                .identification(p.getIdentification())
                .access(p.getAccess())
                .situation(p.getSituation())
                .category(p.getCategory())
                .occupant(p.getOccupant())
                .individualStatus(p.getIndividualStatus())
                .interviewerCanProcess(p.getInterviewerCanProcess())
                .numberOfRespondents(p.getNumberOfRespondents())
                .presentInPreviousHome(p.getPresentInPreviousHome())
                .householdComposition(p.getHouseholdComposition())
                .build();
    }

    @Override
    public List<SurveyUnitToCloseResponse> empty() {
        return List.of();
    }
}
