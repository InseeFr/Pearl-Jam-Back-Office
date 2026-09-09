package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.campaign.service.dummy.FixedDateService;
import fr.insee.pearljam.domain.reporting.port.out.ComputeCampaignSnapshotPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CampaignProgressSnapshotServiceTest {

    static final LocalDate TEST_DAY = LocalDate.of(2025, 6, 15);

    DateService dateService;
    ComputeCampaignSnapshotPort computeCampaignSnapshotPort;
    CampaignProgressSnapshotService service;

    @BeforeEach
    void setup() {
        dateService = new FixedDateService();
        computeCampaignSnapshotPort = mock(ComputeCampaignSnapshotPort.class);
        service = new CampaignProgressSnapshotService(dateService, computeCampaignSnapshotPort);
    }

    @Test
    @DisplayName("Should delegate to port with correct date and fixed instant")
    void shouldDelegateToPort() {
        service.computeSnapshot(TEST_DAY);
        verify(computeCampaignSnapshotPort).computeAndStoreSnapshot(TEST_DAY, dateService.now());
    }
}
