package fr.insee.pearljam.domain.surveyunit.readmodel;

public interface SurveyUnitCompletedView {
        String getSurveyUnitId();
        String getSurveyUnitDisplayName();
        String getInterviewerFirstName();
        String getInterviewerLastName();
        String getEndDate();
        String getContactOutcome();
        String getClosingCauseType();
        Boolean getRead();
        String getReadOnlyUrl();
        String getComment();
}

