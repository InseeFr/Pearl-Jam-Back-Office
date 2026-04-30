package fr.insee.pearljam.api.reporting.export.campaignorganization;

import fr.insee.pearljam.api.reporting.export.csv.CsvRow;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationPort;
import fr.insee.pearljam.domain.campaign.port.in.CampaignOrganizationStatsPresenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CampaignOrganizationCsvExporterTest {

    private CampaignOrganizationCsvExporter exporter;
    private CampaignOrganizationPort port;
    private CampaignOrganizationCsvPresenter presenter;

    @BeforeEach
    void setup() {
        port = mock(CampaignOrganizationPort.class);
        presenter = mock(CampaignOrganizationCsvPresenter.class);
        exporter = new CampaignOrganizationCsvExporter(port, presenter);
    }

    @Test
    @DisplayName("Returns CSV with headers and Non attribuées/Total Site rows when no interviewers")
    void shouldReturnCsvWithHeadersAndDefaultRows_whenNoInterviewers() {
        // Given
        CampaignOrganizationCsv csv = createCsv("Test Campaign", List.of());
        when(port.getCampaignOrganization(eq("user1"), eq("camp-1"), any(CampaignOrganizationStatsPresenter.class)))
                .thenReturn(csv);

        // When
        ResponseEntity<byte[]> result = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String csvString = new String(result.getBody());
        String[] lines = csvString.split("\r\n");
        assertThat(lines).hasSize(3);
        assertThat(lines[0]).contains("Nom Prénom Enquêteur;Idep Enquêteur;Nombre d'UE");
        assertThat(lines[1]).contains("Non attribuées");
        assertThat(lines[2]).contains("Total Site");
    }

    @Test
    @DisplayName("Returns CSV with interviewer rows, Non attribuées and Total Site rows")
    void shouldReturnCsvWithInterviewerRows() {
        // Given
        List<CsvRow> rows = new ArrayList<>();
        rows.add(CsvRow.from("John Doe", "ID001", 10L));
        rows.add(CsvRow.from(CampaignOrganizationCsv.NOT_AFFECTED, "", 5L));
        rows.add(CsvRow.from(CampaignOrganizationCsv.TOTAL_SITE, "", 15L));
        CampaignOrganizationCsv csv = new CampaignOrganizationCsv("Test Campaign", rows);
        when(port.getCampaignOrganization(eq("user1"), eq("camp-1"), any(CampaignOrganizationStatsPresenter.class)))
                .thenReturn(csv);

        // When
        ResponseEntity<byte[]> result = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String csvString = new String(result.getBody());
        String[] lines = csvString.split("\r\n");
        assertThat(lines).hasSize(4);
        assertThat(lines[1]).contains("John Doe;ID001;10");
        assertThat(lines[2]).contains("Non attribuées");
        assertThat(lines[3]).contains("Total Site");
    }

    @ParameterizedTest
    @MethodSource("provideCampaignLabelScenarios")
    @DisplayName("Generates correct filename with campaign label and date")
    void shouldGenerateCorrectFilename(String campaignLabel, String expectedFilenamePart, LocalDate date) {
        // Given
        CampaignOrganizationCsv csv = createCsv(campaignLabel, List.of());
        when(port.getCampaignOrganization(eq("user1"), eq("camp-1"), any(CampaignOrganizationStatsPresenter.class)))
                .thenReturn(csv);

        // When
        ResponseEntity<byte[]> result = exporter.export("user1", "camp-1", date);

        // Then
        String contentDisposition = result.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains(expectedFilenamePart);
    }

    private static Stream<Arguments> provideCampaignLabelScenarios() {
        return Stream.of(
                Arguments.of("My_Campaign", "My_Campaign_Repartition_enqueteurs_10062025.csv", LocalDate.of(2025, 6, 10)),
                Arguments.of("Enquête Test", "Enquête Test_Repartition_enqueteurs_10062025.csv", LocalDate.of(2025, 6, 10)),
                Arguments.of("Enquête-Test_2025", "Enquête-Test_2025_Repartition_enqueteurs_10062025.csv", LocalDate.of(2025, 6, 10))
        );
    }

    @Test
    @DisplayName("Returns CSV starting with the UTF-8 BOM")
    void shouldReturnCsvStartingWithBom() {
        // Given
        CampaignOrganizationCsv csv = createCsv("Test Campaign", List.of());
        when(port.getCampaignOrganization(eq("user1"), eq("camp-1"), any(CampaignOrganizationStatsPresenter.class)))
                .thenReturn(csv);

        // When
        ResponseEntity<byte[]> result = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String csvString = new String(result.getBody());
        assertThat(csvString).startsWith("\uFEFF");
    }

    @Test
    @DisplayName("Returns text/plain content type")
    void shouldReturnTextPlainContentType() {
        // Given
        CampaignOrganizationCsv csv = createCsv("Test Campaign", List.of());
        when(port.getCampaignOrganization(eq("user1"), eq("camp-1"), any(CampaignOrganizationStatsPresenter.class)))
                .thenReturn(csv);

        // When
        ResponseEntity<byte[]> result = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        assertThat(result.getHeaders().getContentType()).hasToString("text/plain");
    }

    @Test
    @DisplayName("Calls port with correct userId, campaignId and presenter parameters")
    void shouldCallPortWithCorrectParameters() {
        // Given
        CampaignOrganizationCsv csv = createCsv("Test Campaign", List.of());
        when(port.getCampaignOrganization(eq("test-user"), eq("test-campaign"), any(CampaignOrganizationStatsPresenter.class)))
                .thenReturn(csv);

        // When
        exporter.export("test-user", "test-campaign", LocalDate.of(2025, 6, 10));

        // Then
        verify(port).getCampaignOrganization("test-user", "test-campaign", eq(presenter));
    }

    @Test
    @DisplayName("Returns HTTP 200 OK status")
    void shouldReturnHttp200Ok() {
        // Given
        CampaignOrganizationCsv csv = createCsv("Test Campaign", List.of());
        when(port.getCampaignOrganization(eq("user1"), eq("camp-1"), any(CampaignOrganizationStatsPresenter.class)))
                .thenReturn(csv);

        // When
        ResponseEntity<byte[]> result = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        assertThat(result.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);
    }

    @Test
    @DisplayName("Handles multiple interviewers correctly")
    void shouldHandleMultipleInterviewers() {
        // Given
        List<CsvRow> rows = new ArrayList<>();
        rows.add(CsvRow.from("Alice Smith", "ID001", 5L));
        rows.add(CsvRow.from("Bob Jones", "ID002", 8L));
        rows.add(CsvRow.from(CampaignOrganizationCsv.NOT_AFFECTED, "", 5L));
        rows.add(CsvRow.from(CampaignOrganizationCsv.TOTAL_SITE, "", 13L));
        CampaignOrganizationCsv csv = new CampaignOrganizationCsv("Test Campaign", rows);
        when(port.getCampaignOrganization(eq("user1"), eq("camp-1"), any(CampaignOrganizationStatsPresenter.class)))
                .thenReturn(csv);

        // When
        ResponseEntity<byte[]> result = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String csvString = new String(result.getBody());
        String[] lines = csvString.split("\r\n");
        assertThat(lines).hasSize(5);
        assertThat(lines[1]).contains("Alice Smith;ID001;5");
        assertThat(lines[2]).contains("Bob Jones;ID002;8");
    }

    private CampaignOrganizationCsv createCsv(String campaignLabel, List<CsvRow> additionalRows) {
        List<CsvRow> rows = new ArrayList<>(additionalRows);
        rows.add(CsvRow.from(CampaignOrganizationCsv.NOT_AFFECTED, "", 5L));
        rows.add(CsvRow.from(CampaignOrganizationCsv.TOTAL_SITE, "", 15L));
        return new CampaignOrganizationCsv(campaignLabel, rows);
    }
}
