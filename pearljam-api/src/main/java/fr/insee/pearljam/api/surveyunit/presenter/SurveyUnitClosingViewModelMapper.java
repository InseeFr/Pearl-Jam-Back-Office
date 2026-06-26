package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.contracts.constants.Constants;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.IdentificationState;
import fr.insee.pearljam.domain.surveyunit.model.contactoutcome.ContactOutcomeType;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SurveyUnitClosingViewModelMapper {

    public SurveyUnitClosingViewModel map(
            ClosableSurveyUnitView projection,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates
    ) {

        String id = projection.getId();

        ClosableSurveyUnitCandidateView candidate = candidatesById.get(id);

        ContactOutcomeType contactOutcome =
                candidate != null ? candidate.getContactOutcomeType() : null;

        String questionnaireState = questionnaireStates.getOrDefault(
                id,
                Constants.QUESTIONNAIRE_STATE_UNAVAILABLE
        );

        return new SurveyUnitClosingViewModel(
                projection.getCampaignLabel(),
                projection.getDisplayName(),
                projection.getId(),
                buildInterviewerLabel(projection),
                projection.getInterviewerId(),
                projection.getSsech(),
                computeIdentificationState(projection),
                contactOutcome,
                questionnaireState,
                projection.getClosingCauseType()
        );
    }

    private String buildInterviewerLabel(ClosableSurveyUnitView projection) {
        String firstName = projection.getInterviewerFirstName();
        String lastName = projection.getInterviewerLastName();

        if (firstName == null && lastName == null) {
            return null;
        }

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

        if (allNull) {
            return null;
        }

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
}