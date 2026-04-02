package fr.insee.pearljam.domain.reporting.readmodel.progress;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CampaignPhase {
    INITIAL_ASSIGNMENT,
    COLLECTION_IN_PROGRESS,
    COLLECTION_COMPLETED;


    public static CampaignPhase fromDates(
            long currentDate,
            long managementStartDate,
            long collectionStartDate,
            long collectionEndDate,
            long endDate
    ) {

        if (currentDate < managementStartDate) {
            throw new IllegalStateException("Campaign not visible yet");
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
