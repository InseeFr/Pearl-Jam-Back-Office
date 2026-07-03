package fr.insee.pearljam.api.surveyunit.presenter;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.api.surveyunit.csv.SurveyUnitAssignedCsv;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitAssignedPresenter;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitAssigned;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SurveyUnitAssignedCsvPresenter implements SurveyUnitAssignedPresenter<SurveyUnitAssignedCsv> {

    @Override
    public SurveyUnitAssignedCsv present(Page<SurveyUnitAssigned> surveyUnits) {
        if (surveyUnits.getTotalPages() != 1)
            throw new IllegalArgumentException("Survey unit data for CSV format shouldn't be paginated.");
        List<CsvRow> rows = surveyUnits.getContent().stream()
                .map(SurveyUnitAssignedCsv::toCsv)
                .toList();
        return new SurveyUnitAssignedCsv(rows);
    }

}
