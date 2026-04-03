package fr.insee.pearljam.domain.reporting.readmodel.collect;

public record CampaignCollection(String campaignId,
                                 String campaignLabel,
                                 long allocated,
                                 CollectionRates rates,
                                 ContactOutcomesProgress outcomes,
                                 ClosingCausesProgress closingCauses) {
}
