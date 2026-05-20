package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.collection.CollectionCsvRow.*;
import static fr.insee.pearljam.api.reporting.export.csv.CsvRow.*;

@Component
public class InterviewerCollectionCsvPresenter
        implements CampaignStatsByInterviewersPresenter<InterviewerCollectionCsv> {

    @Override
    public InterviewerCollectionCsv present(List<InterviewerDailyStats> interviewerStats,
                                            CampaignDailyStats siteStats,
                                            CampaignDailyStats campaignStats) {

        // todo Suivre Enquête Collecte Enquêteurs ->
        //  Colonne Confiée au lieu de Confiée Enquêteur, Colonne "nom Prénom" au lieu de "Nom Prénom enquêteur" -> done,
        //  Manque ligne UE non affectées, Total Site et Total France -> done

        List<CsvRow> rows = new ArrayList<>();
        interviewerStats.forEach(interv ->
                addRowWithMultipleColumnLabel(rows,
                        List.of(interv.getInterviewerFirstName() + " " + interv.getInterviewerLastName(), interv.getInterviewerId()),
                        CollectionCsvRow.commonValues(interv)));

        addRowWithLabel(rows, TOTAL_UNAFFECTED,
                // 1 Column for TOTAL_UNAFFECTED
                // followed by 1 Column for Idep + Common values columns with emptyRowWithValueAtSpecificPosition
                emptyRowWithValueAtSpecificPosition(campaignStats.getUnaffectedCount(),
                        CollectionCsvRow.commonValuesSize(), CollectionCsvRow.commonValuesSize() + 1));
        addRowWithLabel(rows, TOTAL_FRANCE, CollectionCsvRow.commonValues((campaignStats)));
        addRowWithLabel(rows, TOTAL_SITE, CollectionCsvRow.commonValues((siteStats)));

        return new InterviewerCollectionCsv(rows);
    }
}
