package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitToCloseStatsPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

                    return SurveyUnitToCloseResponse.from(
                            candidatesById.get(id),
                            projection,
                            questionnaireStates.getOrDefault(
                                    id,
                                    QUESTIONNAIRE_STATE_UNAVAILABLE
                            )
                    );
                })
                .toList();
    }

    @Override
    public List<SurveyUnitToCloseResponse> empty() {
        return List.of();
    }
}
