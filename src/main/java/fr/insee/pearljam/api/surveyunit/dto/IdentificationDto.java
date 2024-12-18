package fr.insee.pearljam.api.surveyunit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.question.*;

public record IdentificationDto(
        IdentificationQuestionValue identification,
        AccessQuestionValue access,
        SituationQuestionValue situation,
        CategoryQuestionValue category,
        OccupantQuestionValue occupant,
        @JsonProperty("individual_status")
        IndividualStatusQuestionValue individualStatus,
        @JsonProperty("interviewer_can_process")
        InterviewerCanProcessQuestionValue interviewerCanProcess,
        @JsonProperty("number_of_respondents")
        NumberOfRespondentsQuestionValue numberOfRespondents,
        @JsonProperty("present_in_previous_home")
        PresentInPreviousHomeQuestionValue presentInPreviousHome,
        @JsonProperty("household_composition")
        HouseholdCompositionQuestionValue householdComposition
) {

    public static Identification toModel(IdentificationDto identificationDto) {
        if (identificationDto == null) {
            return null;
        }

        return new Identification(
                identificationDto.identification(),
                identificationDto.access(),
                identificationDto.situation(),
                identificationDto.category(),
                identificationDto.occupant(),
                identificationDto.individualStatus(),
                identificationDto.interviewerCanProcess(),
                identificationDto.numberOfRespondents(),
                identificationDto.presentInPreviousHome(),
                identificationDto.householdComposition()
        );
    }

    public static IdentificationDto fromModel(Identification identification) {
        if (identification == null) {
            return null;
        }
        return new IdentificationDto(identification.identification(),
                identification.access(),
                identification.situation(),
                identification.category(),
                identification.occupant(),
                identification.individualStatus(),
                identification.interviewerCanProcess(),
                identification.numberOfRespondents(),
                identification.presentInPreviousHome(),
                identification.householdComposition()
        );
    }
}
