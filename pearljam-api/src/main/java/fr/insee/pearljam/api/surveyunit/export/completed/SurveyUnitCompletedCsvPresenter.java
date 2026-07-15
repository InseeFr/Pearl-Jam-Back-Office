package fr.insee.pearljam.api.surveyunit.export.completed;

import fr.insee.pearljam.api.export.csv.CsvRow;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitCompletedPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import jakarta.annotation.Nullable;
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
                        formatViewedInFrench(su.viewed()),
                        removeCarriageReturnsFromComment(su.comment())
                ))
                .toList();

        return new SurveyUnitCompletedCsv(rows);
    }

    private String formatViewedInFrench(@Nullable Boolean isViewed)
    {
        if(isViewed == null)
        {
            return "Non";
        }

        return isViewed ? "Oui" : "Non";
    }

    private String removeCarriageReturnsFromComment(@Nullable  String comment)
    {
        if(comment == null)
        {
            return comment;
        }

        comment = comment.replace("\\n", "");
        comment = comment.replace("\\r", "");
        return  comment;
    }

    @Override
    public SurveyUnitCompletedCsv empty() {
        return new SurveyUnitCompletedCsv(List.of());
    }
}
