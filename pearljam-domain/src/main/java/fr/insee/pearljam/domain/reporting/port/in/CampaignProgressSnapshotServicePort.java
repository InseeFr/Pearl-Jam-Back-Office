package fr.insee.pearljam.domain.reporting.port.in;

import java.time.LocalDate;

public interface CampaignProgressSnapshotServicePort {
    void computeSnapshot(LocalDate day);
}
