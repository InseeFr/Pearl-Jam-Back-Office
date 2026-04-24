package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
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

                    String identificationState =
                            candidate != null && candidate.getCurrentStateType() != null
                                    ? candidate.getCurrentStateType().name()
                                    : null;

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
                            identificationState,
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

    @Override
    public List<SurveyUnitToCloseResponse> empty() {
        return List.of();
    }
}
