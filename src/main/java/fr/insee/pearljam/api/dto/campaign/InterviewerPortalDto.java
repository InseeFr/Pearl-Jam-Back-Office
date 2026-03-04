package fr.insee.pearljam.api.dto.campaign;

import fr.insee.pearljam.domain.count.model.InterviewerCount;

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