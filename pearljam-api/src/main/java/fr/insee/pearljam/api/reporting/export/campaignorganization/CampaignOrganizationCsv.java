package fr.insee.pearljam.api.reporting.export.campaignorganization;

import fr.insee.pearljam.api.campaign.response.CampaignOrganizationResponse;
import fr.insee.pearljam.api.export.csv.CsvExportable;
import fr.insee.pearljam.api.export.csv.CsvRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record CampaignOrganizationCsv(String campaignId, List<CsvRow> rows) implements CsvExportable {

     static final String NOT_AFFECTED = "Non attribuées";
     static final String TOTAL_SITE = "Total Site";

    public static CampaignOrganizationCsv from(CampaignOrganizationResponse response) {
        List<CsvRow> rows = new ArrayList<>();

        // Add rows for each interviewer
        response.interviewers().forEach(interviewer -> rows.add(CsvRow.from(
                interviewer.label(),
                interviewer.id(),
                interviewer.surveyUnits()
        )));

        // Add NOT_AFFECTED row
        rows.add(CsvRow.from(
                NOT_AFFECTED,
                "",
                response.surveyUnits().notAffected()
        ));

        // Add TOTAL_SITE row
        rows.add(CsvRow.from(
                TOTAL_SITE,
                "",
                response.surveyUnits().totalSite()
        ));

        return new CampaignOrganizationCsv(response.campaignId(), rows);
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
