package fr.insee.pearljam.domain.count.model;

public record ClosingCauseCount(
        String entityId,
        Long npaCount,
        Long npiCount,
        Long npxCount,
        Long rowCount) {}
