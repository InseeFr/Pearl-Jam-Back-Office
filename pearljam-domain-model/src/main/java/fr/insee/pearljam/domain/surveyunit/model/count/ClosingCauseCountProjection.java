package fr.insee.pearljam.domain.surveyunit.model.count;

public record ClosingCauseCountProjection(
        String entityId,
        Long npaCount,
        Long npiCount,
        Long npxCount,
        Long rowCount) {}
