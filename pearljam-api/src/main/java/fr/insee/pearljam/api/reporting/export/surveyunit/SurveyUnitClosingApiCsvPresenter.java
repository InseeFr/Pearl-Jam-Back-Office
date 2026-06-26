package fr.insee.pearljam.api.reporting.export.surveyunit;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.surveyunit.presenter.SurveyUnitClosingViewModelMapper;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitCandidateView;
import fr.insee.pearljam.domain.surveyunit.port.out.view.ClosableSurveyUnitView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.addRowWithTitleLabel;

@Component
public class SurveyUnitClosingApiCsvPresenter
        implements SurveyUnitClosingPresenter<SurveyUnitClosingCsv> {

    private final SurveyUnitClosingViewModelMapper mapper;

    public SurveyUnitClosingApiCsvPresenter(SurveyUnitClosingViewModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public SurveyUnitClosingCsv present(
            List<ClosableSurveyUnitView> projections,
            Map<String, ClosableSurveyUnitCandidateView> candidatesById,
            Map<String, String> questionnaireStates
    ) {

        List<CsvRow> rows = new ArrayList<>();

        projections.stream()
                .map(p -> mapper.map(p, candidatesById, questionnaireStates))
                .forEach(vm ->
                        addRowWithTitleLabel(
                                rows,
                                vm.campaignLabel(),
                                List.of(
                                        vm.displayName(),
                                        vm.id(),
                                        vm.interviewerLabel(),
                                        vm.interviewerId(),
                                        vm.ssech(),
                                        vm.identificationState().name(),
                                        vm.contactOutcome(),
                                        vm.questionnaireState(),
                                        vm.closingCauseType()
                                )
                        ));

        return new SurveyUnitClosingCsv(rows);
    }

    @Override
    public SurveyUnitClosingCsv empty() {
        return new SurveyUnitClosingCsv(List.of());
    }
}