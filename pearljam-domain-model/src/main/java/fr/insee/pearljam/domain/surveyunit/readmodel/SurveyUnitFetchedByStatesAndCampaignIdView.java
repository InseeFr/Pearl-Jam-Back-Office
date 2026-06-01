package fr.insee.pearljam.domain.surveyunit.readmodel;

public interface SurveyUnitFetchedByStatesAndCampaignIdView {
        String getSurveyUnitId();
        String getSurveyUnitDisplayName();
        String getInterviewerFirstName();
        String getInterviewerLastName();
        String getEndDate();
        String getContactOutcome();
        String getClosingCauseType();
        Boolean getViewed();
        String getReadOnlyUrl();
        String getComment();
}

