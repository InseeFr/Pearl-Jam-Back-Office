package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.surveyunit.response.SurveyUnitToCloseResponse;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SurveyUnitClosingApiPresenter
        implements SurveyUnitClosingPresenter<List<SurveyUnitToCloseResponse>> {

    private final SurveyUnitClosingViewModelMapper mapper;

    public SurveyUnitClosingApiPresenter(SurveyUnitClosingViewModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<SurveyUnitToCloseResponse> present(
            List<ClosableSurveyUnitView> projections,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates
    ) {

        return projections.stream()
                .map(p -> mapper.map(p, candidatesById, questionnaireStates))
                .map(vm -> new SurveyUnitToCloseResponse(
                        vm.campaignLabel(),
                        vm.id(),
                        vm.displayName(),
                        vm.interviewerLabel(),
                        vm.ssech(),
                        vm.identificationState().name(),
                        vm.contactOutcome(),
                        vm.questionnaireState(),
                        vm.closingCauseType()
                ))
                .toList();
    }

    @Override
    public List<SurveyUnitToCloseResponse> empty() {
        return List.of();
    }
}