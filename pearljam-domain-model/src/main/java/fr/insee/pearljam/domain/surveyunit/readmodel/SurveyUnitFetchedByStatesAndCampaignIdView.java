package fr.insee.pearljam.domain.surveyunit.readmodel;

public record SurveyUnitFetchedByStatesAndCampaignIdView(
        String surveyUnitId,
        String surveyUnitDisplayName,
        String interviewerFirstName,
        String interviewerLastName,
        String interviewerId,
        String endDate,
        String contactOutcome,
        String closingCauseType,
        Boolean viewed,
        String comment
) {}