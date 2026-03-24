package fr.insee.pearljam.contracts.campaign.dto;

import fr.insee.pearljam.domain.surveyunit.model.count.InterviewerCount;

public record InterviewerPortalDto(
    String firstName,
    String lastName,
    int count
) {
    public static InterviewerPortalDto fromModel(InterviewerCount interviewerCount) {
        return new InterviewerPortalDto(
            interviewerCount.firstName(),
            interviewerCount.lastName(),
            interviewerCount.surveyUnitCount().intValue()
        );
    }
}
