package fr.insee.pearljam.infrastructure.persistence.reporting.adapter;

import fr.insee.pearljam.domain.reporting.port.out.ComputeCampaignSnapshotPort;
import fr.insee.pearljam.infrastructure.persistence.reporting.batch.CampaignProgressSnapshotRepository;
import fr.insee.pearljam.infrastructure.persistence.reporting.batch.PartitionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CampaignProgressSnapshotAdapter implements ComputeCampaignSnapshotPort {

    private final PartitionManager partitionManager;
    private final CampaignProgressSnapshotRepository snapshotRepository;

    @Override
    public void computeAndStoreSnapshot(LocalDate day, Instant updatedAt) {
        partitionManager.ensureMonthlyPartitionExists(day);
        snapshotRepository.computeAndStoreSnapshot(day, updatedAt);
    }
}
