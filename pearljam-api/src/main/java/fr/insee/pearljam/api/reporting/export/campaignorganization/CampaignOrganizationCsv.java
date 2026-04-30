package fr.insee.pearljam.api.reporting.export.campaignorganization;

import fr.insee.pearljam.api.campaign.response.CampaignOrganizationResponse;
import fr.insee.pearljam.api.reporting.export.csv.CsvExportable;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record CampaignOrganizationCsv(List<CsvRow> rows) implements CsvExportable {

    private static final String NON_ATTRIBUEES = "Non attribuées";
    private static final String TOTAL_SITE = "Total Site";

    public static CampaignOrganizationCsv from(CampaignOrganizationResponse response) {
        List<CsvRow> rows = new ArrayList<>();

        // Add rows for each interviewer
        response.interviewers().forEach(interviewer -> rows.add(CsvRow.from(
                interviewer.label(),
                interviewer.id(),
                interviewer.surveyUnits()
        )));

        // Add "Non attribuées" row
        rows.add(CsvRow.from(
                NON_ATTRIBUEES,
                "",
                response.surveyUnits().notAffected()
        ));

        // Add "Total Site" row
        rows.add(CsvRow.from(
                TOTAL_SITE,
                "",
                response.surveyUnits().total()
        ));

        return new CampaignOrganizationCsv(rows);
    }

    @Override
    public CsvRow headers() {
        return CsvRow.from(
                Arrays.stream(CampaignOrganizationCsvHeaders.values())
                        .map(CampaignOrganizationCsvHeaders::getHeaderName)
                        .toArray()
        );
    }
}
