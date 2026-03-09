package fr.insee.pearljam.domain.campaign.model;

public record SurveyUnitCounts(
    int abandoned,
    int unallocated,
    int total
) {}
