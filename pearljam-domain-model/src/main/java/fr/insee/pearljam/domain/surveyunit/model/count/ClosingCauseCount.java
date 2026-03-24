package fr.insee.pearljam.domain.surveyunit.model.count;

public record ClosingCauseCount(
        String entityId,
        Long npaCount,
        Long npiCount,
        Long npxCount,
        Long rowCount) {}
