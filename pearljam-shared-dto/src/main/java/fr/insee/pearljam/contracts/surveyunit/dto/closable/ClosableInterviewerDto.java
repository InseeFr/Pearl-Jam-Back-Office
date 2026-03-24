package fr.insee.pearljam.contracts.surveyunit.dto.closable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClosableInterviewerDto(String interviewerFirstName, String interviewerLastName) {
}
