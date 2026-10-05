package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.reporting.port.in.CampaignProgressSnapshotServicePort;
import fr.insee.pearljam.domain.reporting.port.out.ComputeCampaignSnapshotPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CampaignProgressSnapshotService implements CampaignProgressSnapshotServicePort {

    private final DateService dateService;
    private final ComputeCampaignSnapshotPort computeCampaignSnapshotPort;

    @Override
    public void computeSnapshot(LocalDate day) {
        computeCampaignSnapshotPort.computeAndStoreSnapshot(day, dateService.now());
    }
}
