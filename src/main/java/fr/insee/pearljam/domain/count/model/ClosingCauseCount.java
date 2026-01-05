package fr.insee.pearljam.domain.count.model;

public record ClosingCauseCount(
        String campaignId,
        Long npaCount,
        Long npiCount,
        Long npxCount,
        Long rowCount) {}
