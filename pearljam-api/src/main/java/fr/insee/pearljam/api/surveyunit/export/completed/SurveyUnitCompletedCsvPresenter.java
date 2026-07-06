package fr.insee.pearljam.api.surveyunit.export.completed;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitCompletedPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SurveyUnitCompletedCsvPresenter implements SurveyUnitCompletedPresenter<SurveyUnitCompletedCsv> {
    @Override
    public SurveyUnitCompletedCsv present(Page<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits) {
        List<CsvRow> rows = surveyUnits.getContent().stream()
                .map(su -> CsvRow.from(
                        su.surveyUnitId(),
                        su.surveyUnitDisplayName(),
                        getInterviewerLabel(su),
                        su.interviewerId(),
                        su.endDate(),
                        su.contactOutcome(),
                        su.closingCauseType(),
                        su.viewed() != null ? su.viewed().toString() : "",
                        su.comment()
                ))
                .toList();

        return new SurveyUnitCompletedCsv(rows);
    }

    @Override
    public SurveyUnitCompletedCsv empty() {
        return new SurveyUnitCompletedCsv(List.of());
    }
}
