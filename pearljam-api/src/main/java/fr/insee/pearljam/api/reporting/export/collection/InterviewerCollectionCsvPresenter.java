package fr.insee.pearljam.api.reporting.export.collection;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.reporting.port.in.CampaignStatsByInterviewersPresenter;
import fr.insee.pearljam.domain.reporting.readmodel.CampaignDailyStats;
import fr.insee.pearljam.domain.reporting.readmodel.InterviewerDailyStats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static fr.insee.pearljam.api.reporting.export.collection.CollectionCsvRow.TOTAL_FRANCE;
import static fr.insee.pearljam.api.reporting.export.collection.OrganizationUnitCollectionCsv.TOTAL_FRANCE;

@Component
public class InterviewerCollectionCsvPresenter
        implements CampaignStatsByInterviewersPresenter<InterviewerCollectionCsv> {

    @Override
    public InterviewerCollectionCsv present(List<InterviewerDailyStats> interviewerStats,
                                            CampaignDailyStats siteStats,
                                            CampaignDailyStats campaignStats) {


        // todo Suivre Enquête Collecte Enquêteurs ->
        //  Colonne Confiée au lieu de Confiée Enquêteur, Colonne "nom Prénom" au lieu de "Nom Prénom enquêteur" -> done,
        //  Manque ligne UE non affectées, Total Site et Total France

        List<CsvRow> rows = new ArrayList<>();
                interviewerStats.forEach(interviewer -> {
                    List<Object> values = new ArrayList<>();
                    values.add(interviewer.getInterviewerFirstName() + " " + interviewer.getInterviewerLastName());
                    values.add(interviewer.getInterviewerId());
                    values.addAll(CollectionCsvRow.commonValues(interviewer));
                    rows.add(CsvRow.from(values.toArray()));
                });


        List<String> list = new ArrayList<>(Collections.nCopies(CollectionCsvRow.commonValuesSize() - 2, ""));
        List<Object> rowData = new ArrayList<>();
        rowData.add(TOTAL_FRANCE);
        rowData.addAll(list);
        rowData.add(campaignStats.getAllocatedCount());
        rows.add(CsvRow.from(rowData.toArray()));

        return new InterviewerCollectionCsv(rows);
    }
}
