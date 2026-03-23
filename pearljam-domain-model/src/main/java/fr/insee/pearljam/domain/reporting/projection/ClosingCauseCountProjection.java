package fr.insee.pearljam.domain.reporting.projection;

public record ClosingCauseCountProjection(
        String entityId,
        Long npaCount,
        Long npiCount,
        Long npxCount,
        Long rowCount) {}
