package fr.insee.pearljam.domain.reporting.port.out;

import java.time.Instant;
import java.time.LocalDate;

public interface ComputeCampaignSnapshotPort {
    void computeAndStoreSnapshot(LocalDate day, Instant updatedAt);
}
