package fr.insee.pearljam.api.reporting.controller;

import fr.insee.pearljam.api.reporting.export.campaignorganization.CampaignOrganizationCsvExporter;
import fr.insee.pearljam.domain.campaign.service.exception.CampaignNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignOrganizationExportControllerTest {

    @Mock
    private CampaignOrganizationCsvExporter csvExporter;

    private CampaignOrganizationExportController controller;

    @BeforeEach
    void init() {
        controller = new CampaignOrganizationExportController(csvExporter);
    }

    @Test
    @DisplayName("When export called with valid parameters, should delegate to csvExporter and return response")
    void testExportCampaignOrganizationAsCsv01_Delegation() {
        // Given
        String campaignId = "camp-1";
        LocalDate date = LocalDate.of(2025, 6, 10);
        String userId = "user-1";
        byte[] csvContent = "test,csv,content".getBytes();
        ResponseEntity<byte[]> expectedResponse = ResponseEntity.ok().body(csvContent);

        when(csvExporter.export(userId, campaignId, date))
                .thenReturn(expectedResponse);

        // When
        ResponseEntity<byte[]> result = controller.exportCampaignOrganizationAsCsv(campaignId, date, userId);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        verify(csvExporter).export(userId, campaignId, date);
    }

    @Test
    @DisplayName("When export called, should propagate CampaignNotFoundException from csvExporter")
    void testExportCampaignOrganizationAsCsv02_ExceptionPropagation() {
        // Given
        String campaignId = "non-existent";
        LocalDate date = LocalDate.of(2025, 6, 10);
        String userId = "user-1";

        when(csvExporter.export(userId, campaignId, date))
                .thenThrow(new CampaignNotFoundException());

        // When/Then
        assertThatThrownBy(() -> controller.exportCampaignOrganizationAsCsv(campaignId, date, userId))
                .isInstanceOf(CampaignNotFoundException.class);
        verify(csvExporter).export(userId, campaignId, date);
    }

    @Test
    @DisplayName("When controller instantiated, should have non-null csvExporter")
    void testCampaignOrganizationExportController03_Constructor() {
        // Given/When
        CampaignOrganizationExportController testController = new CampaignOrganizationExportController(csvExporter);

        // Then - Lombok @RequiredArgsConstructor generates constructor that assigns the field
        assertThat(testController).isNotNull();
    }
}
