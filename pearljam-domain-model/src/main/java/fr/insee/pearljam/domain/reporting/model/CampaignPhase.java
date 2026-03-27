package fr.insee.pearljam.domain.reporting.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CampaignPhase {
    INITIAL_ASSIGNMENT("Affectation initiale"),
    COLLECTION_IN_PROGRESS("Collecte en cours"),
    COLLECTION_COMPLETED("Collecte terminée");

    private final String label;

    public static CampaignPhase fromDates(
            long currentDate,
            long managementStartDate,
            long collectionStartDate,
            long collectionEndDate,
            long endDate
    ) {

        if (currentDate < managementStartDate) {
            throw new IllegalStateException("Campaign not yet visible");
        }

        if (currentDate < collectionStartDate) {
            return CampaignPhase.INITIAL_ASSIGNMENT;
        }

        if (currentDate < collectionEndDate) {
            return CampaignPhase.COLLECTION_IN_PROGRESS;
        }

        if (currentDate <= endDate) {
            return CampaignPhase.COLLECTION_COMPLETED;
        }

        throw new IllegalStateException("Campaign already terminated");
    }
}
