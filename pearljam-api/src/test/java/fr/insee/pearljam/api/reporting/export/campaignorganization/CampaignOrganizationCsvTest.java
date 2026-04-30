package fr.insee.pearljam.api.reporting.export.campaignorganization;



import fr.insee.pearljam.api.campaign.response.CampaignOrganizationResponse;
import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import org.junit.jupiter.api.Test;



import java.util.List;


import static fr.insee.pearljam.api.reporting.export.campaignorganization.CampaignOrganizationCsv.NOT_AFFECTED;
import static fr.insee.pearljam.api.reporting.export.campaignorganization.CampaignOrganizationCsv.TOTAL_SITE;
import static fr.insee.pearljam.api.reporting.export.campaignorganization.CampaignOrganizationCsvHeaders.*;
import static org.assertj.core.api.Assertions.assertThat;



class CampaignOrganizationCsvTest {

    @Test
    void shouldHaveAllHeadersFromEnum() {
        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(createEmptyResponse());
        CsvRow headers = csv.headers();

        assertThat(headers.values()).containsExactly(
                INTERVIEWER_LABEL.getHeaderName(),
                INTERVIEWER_ID.getHeaderName(),
                SURVEY_UNITS_COUNT.getHeaderName()
        );
    }

    @Test
    void shouldReturnEmptyRows_whenNoInterviewers() {
        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(createEmptyResponse());

        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values()).containsExactly(NOT_AFFECTED, "", "0");
        assertThat(csv.rows().get(1).values()).containsExactly(TOTAL_SITE, "", "0");
    }



    @Test
    void shouldMapSingleInterviewerToRow() {
        CampaignOrganizationResponse.Interviewer interviewer = 
                new CampaignOrganizationResponse.Interviewer("ID001", "John Doe", 10L);
        CampaignOrganizationResponse response = createResponseWithInterviewers(List.of(interviewer));


        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(response);


        assertThat(csv.rows()).hasSize(3);
        assertThat(csv.rows().get(0).values()).containsExactly("John Doe", "ID001", "10");
        assertThat(csv.rows().get(1).values()).containsExactly("Non attribuées", "", "5");
        assertThat(csv.rows().get(2).values()).containsExactly("Total Site", "", "15");
    }



    @Test
    void shouldMapMultipleInterviewersToRows() {
        CampaignOrganizationResponse.Interviewer interviewer1 = 
                new CampaignOrganizationResponse.Interviewer("ID001", "Alice Smith", 5L);
        CampaignOrganizationResponse.Interviewer interviewer2 = 
                new CampaignOrganizationResponse.Interviewer("ID002", "Bob Jones", 8L);
        CampaignOrganizationResponse response = createResponseWithInterviewers(List.of(interviewer1, interviewer2), 15L, 2L);


        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(response);


        assertThat(csv.rows()).hasSize(4);
        assertThat(csv.rows().get(0).values()).containsExactly("Alice Smith", "ID001", "5");
        assertThat(csv.rows().get(1).values()).containsExactly("Bob Jones", "ID002", "8");
        assertThat(csv.rows().get(2).values()).containsExactly("Non attribuées", "", "2");
        assertThat(csv.rows().get(3).values()).containsExactly("Total Site", "", "15");
    }



    @Test
    void shouldIncludeNonAttributedAndTotalRows() {
        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(createEmptyResponse());


        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values().get(0)).isEqualTo("Non attribuées");
        assertThat(csv.rows().get(0).values().get(1)).isEmpty();
        assertThat(csv.rows().get(1).values().get(0)).isEqualTo("Total Site");
        assertThat(csv.rows().get(1).values().get(1)).isEmpty();
    }



    @Test
    void shouldHandleNullSurveyUnitsCount() {
        CampaignOrganizationResponse response = new CampaignOrganizationResponse(
                "camp-1",
                "Campaign 1",
                "email@test.com",
                1L, 1L, 1L, 1L, 1L,
                null,
                List.of(),
                List.of(),
                new CampaignOrganizationResponse.CampaignOrganizationSurveyUnitCount(null, null)
        );


        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(response);


        assertThat(csv.rows()).hasSize(2);
        assertThat(csv.rows().get(0).values()).containsExactly("Non attribuées", "", "");
        assertThat(csv.rows().get(1).values()).containsExactly("Total Site", "", "");
    }



    @Test
    void shouldHaveRowSizeMatchingHeaderSize() {
        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(createEmptyResponse());


        CsvRow headers = csv.headers();
        for (CsvRow row : csv.rows()) {
            assertThat(row.values()).hasSameSizeAs(headers.values());
        }

    }



    @Test
    void shouldHaveRowSizeMatchingHeaderSize_WithInterviewers() {
        CampaignOrganizationResponse.Interviewer interviewer = 
                new CampaignOrganizationResponse.Interviewer("ID001", "Test User", 3L);
        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(
                createResponseWithInterviewers(List.of(interviewer)));


        CsvRow headers = csv.headers();
        for (CsvRow row : csv.rows()) {
            assertThat(row.values()).hasSameSizeAs(headers.values());
        }

    }



    @Test
    void shouldHandleInterviewerWithNullValues() {
        CampaignOrganizationResponse.Interviewer interviewer = 
                new CampaignOrganizationResponse.Interviewer(null, null, null);
        CampaignOrganizationResponse response = createResponseWithInterviewers(List.of(interviewer));


        CampaignOrganizationCsv csv = CampaignOrganizationCsv.from(response);


        assertThat(csv.rows().getFirst().values()).containsExactly("", "", "");
    }



    @Test
    void shouldReturnRowsFromConstructor() {
        List<CsvRow> manualRows = List.of(
                CsvRow.from("Test", "123", "10")
        );
        CampaignOrganizationCsv csv = new CampaignOrganizationCsv(manualRows);


        assertThat(csv.rows()).isEqualTo(manualRows);
    }



    @Test
    void shouldReturnEmptyRowsFromEmptyConstructor() {
        CampaignOrganizationCsv csv = new CampaignOrganizationCsv(List.of());


        assertThat(csv.rows()).isEmpty();
    }



    private CampaignOrganizationResponse createEmptyResponse() {
        return new CampaignOrganizationResponse(
                "camp-1",
                "Campaign 1",
                "email@test.com",
                1L, 1L, 1L, 1L, 1L,
                null,
                List.of(),
                List.of(),
                new CampaignOrganizationResponse.CampaignOrganizationSurveyUnitCount(0L, 0L)
        );
    }



    private CampaignOrganizationResponse createResponseWithInterviewers(
            List<CampaignOrganizationResponse.Interviewer> interviewers) {
        return createResponseWithInterviewers(interviewers, 15L, 5L);
    }

    private CampaignOrganizationResponse createResponseWithInterviewers(
            List<CampaignOrganizationResponse.Interviewer> interviewers, Long total, Long notAffected) {
        return new CampaignOrganizationResponse(
                "camp-1",
                "Campaign 1",
                "email@test.com",
                1L, 1L, 1L, 1L, 1L,
                null,
                List.of(),
                interviewers,
                new CampaignOrganizationResponse.CampaignOrganizationSurveyUnitCount(total, notAffected)
        );
    }

}
