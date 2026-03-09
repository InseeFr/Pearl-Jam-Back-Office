package fr.insee.pearljam.api.surveyunit.dto.interviewer;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterviewerCampaignDto {
    private final String id;
    private final String interviewerFirstName;
    private final String interviewerLastName;
}
