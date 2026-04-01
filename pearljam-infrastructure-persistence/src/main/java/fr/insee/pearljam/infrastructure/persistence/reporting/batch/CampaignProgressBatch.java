package fr.insee.pearljam.infrastructure.persistence.reporting.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CampaignProgressBatch {

    private final PartitionManager partitionManager;
    private final CampaignProgressSnapshot snapshot;

    public void run(LocalDate day) {
        // create partitioned table if not exist within the month range
        partitionManager.ensureMonthlyPartitionExists(day);

        // store snapshot into this table
        snapshot.computeAndStoreSnapshot(day);
    }
}