package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.question.*;

public record IdentificationDto(
        IdentificationQuestionValue identification,
        AccessQuestionValue access,
        SituationQuestionValue situation,
        CategoryQuestionValue category,
        OccupantQuestionValue occupant,
        IndividualStatusQuestionValue individualStatus,
        InterviewerCanProcessQuestionValue interviewerCanProcess,
        NumberOfRespondentsQuestionValue numberOfRespondents,
        PresentInPreviousHomeQuestionValue presentInPreviousHome,
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
