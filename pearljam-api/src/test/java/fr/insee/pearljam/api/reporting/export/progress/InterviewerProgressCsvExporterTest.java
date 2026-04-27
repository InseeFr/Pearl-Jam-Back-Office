package fr.insee.pearljam.api.reporting.export.progress;

import fr.insee.pearljam.api.reporting.presenter.CampaignProgressByInterviewersPresenter;
import fr.insee.pearljam.api.reporting.response.CampaignProgressByInterviewersResponse;
import fr.insee.pearljam.api.reporting.response.CommunicationsProgressResponse;
import fr.insee.pearljam.api.reporting.response.StatesProgressResponse;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import fr.insee.pearljam.domain.reporting.port.in.CampaignReportingByInterviewersPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InterviewerProgressCsvExporterTest {

    private InterviewerProgressCsvExporter exporter;
    private CampaignReportingByInterviewersPort port;

    private static final StatesProgressResponse STATES = new StatesProgressResponse(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    private static final CommunicationsProgressResponse COMMUNICATIONS = new CommunicationsProgressResponse(0, 0);

    @BeforeEach
    void setup() throws CampaignNotFoundException {
        port = mock(CampaignReportingByInterviewersPort.class);
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(emptyResponse());
        exporter = new InterviewerProgressCsvExporter(new CampaignProgressByInterviewersPresenter(), port);
    }

    @Test
    @DisplayName("Returns CSV with headers only when no interviewers are available")
    void shouldReturnCsvWithHeadersOnly_whenNoInterviewers() throws CampaignNotFoundException {
        // Given

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(1);
        assertThat(lines[0]).contains(ProgressCsvHeaders.INTERVIEWER_ID.getHeaderName());
    }

    @Test
    @DisplayName("Returns CSV with data rows when interviewers are available")
    void shouldReturnCsvWithDataRows() throws CampaignNotFoundException {
        // Given
        CampaignProgressByInterviewersResponse data = new CampaignProgressByInterviewersResponse(
                List.of(new CampaignProgressByInterviewersResponse.Interviewer(
                        "JDUP",
                        "Jean Dupont", 75.5f,
                        new StatesProgressResponse(10, 2, 3, 4, 5, 6, 7, 8, 9, 1),
                        new CommunicationsProgressResponse(11, 12))),
                new CampaignProgressByInterviewersResponse.OrganizationUnit(0f, STATES, COMMUNICATIONS),
                new CampaignProgressByInterviewersResponse.Campaign(0, 0f, STATES, COMMUNICATIONS)
        );
        when(port.getProgressForDay(any(), any(), any(), any())).thenReturn(data);

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        assert response.getBody() != null;
        String csv = new String(response.getBody());
        String[] lines = csv.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[1]).startsWith("JDUP;Jean Dupont;75.5;");
    }

    @Test
    @DisplayName("Generates filename with campaign id, user id and date in the Content-Disposition header")
    void shouldGenerateFilenameWithUserIdAndDate() throws CampaignNotFoundException {
        // Given

        // When
        ResponseEntity<byte[]> response = exporter.export("user1", "camp-1", LocalDate.of(2025, 6, 10));

        // Then
        String contentDisposition = response.getHeaders().getFirst("Content-Disposition");
        assertThat(contentDisposition).contains("camp-1_Avancement_enqueteurs_10062025.csv");
    }

    @Test
    @DisplayName("Throws CampaignNotFoundException when campaign does not exist")
    void shouldThrowCampaignNotFoundException_whenCampaignNotFound() throws CampaignNotFoundException {
        // Given
        when(port.getProgressForDay(any(), any(), any(), any())).thenThrow(new CampaignNotFoundException());

        // When / Then
        assertThatThrownBy(() -> exporter.export("user1", "unknown", LocalDate.of(2025, 6, 10)))
                .isInstanceOf(CampaignNotFoundException.class);
    }

    private static CampaignProgressByInterviewersResponse emptyResponse() {
        return new CampaignProgressByInterviewersResponse(
                List.of(),
                new CampaignProgressByInterviewersResponse.OrganizationUnit(0f, STATES, COMMUNICATIONS),
                new CampaignProgressByInterviewersResponse.Campaign(0, 0f, STATES, COMMUNICATIONS)
        );
    }
}
