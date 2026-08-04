package fr.insee.pearljam.domain.surveyunit.port.in;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;

public class SurveyUnitFetchedByStatesAndCampaignIdViewBuilder {

    private String surveyUnitId = "SU-001";
    private String surveyUnitDisplayName = "Display name";
    private String interviewerFirstName = "John";
    private String interviewerLastName = "Doe";
    private String interviewerId = "INTID";
    private String endDate = "2024-01-01";
    private String contactOutcome = "COMPLETED";
    private String closingCauseType = "NONE";
    private Boolean viewed = true;
    private String comment = "comment";

    public static SurveyUnitFetchedByStatesAndCampaignIdViewBuilder aSurveyUnit() {
        return new SurveyUnitFetchedByStatesAndCampaignIdViewBuilder();
    }

    public SurveyUnitFetchedByStatesAndCampaignIdViewBuilder withFirstName(String firstName) {
        this.interviewerFirstName = firstName;
        return this;
    }

    public SurveyUnitFetchedByStatesAndCampaignIdViewBuilder withLastName(String lastName) {
        this.interviewerLastName = lastName;
        return this;
    }

    public SurveyUnitFetchedByStatesAndCampaignIdView build() {
        return new SurveyUnitFetchedByStatesAndCampaignIdView(
                surveyUnitId,
                surveyUnitDisplayName,
                interviewerFirstName,
                interviewerLastName,
                interviewerId,
                endDate,
                contactOutcome,
                closingCauseType,
                viewed,
                comment
        );
    }
}
