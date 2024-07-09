package fr.insee.pearljam.api.surveyunit.dto;

import fr.insee.pearljam.domain.surveyunit.model.Identification;
import fr.insee.pearljam.domain.surveyunit.model.question.AccessQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.CategoryQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.IdentificationQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.OccupantQuestionValue;
import fr.insee.pearljam.domain.surveyunit.model.question.SituationQuestionValue;

public record IdentificationDto(
        IdentificationQuestionValue identification,
        AccessQuestionValue access,
        SituationQuestionValue situation,
        CategoryQuestionValue category,
        OccupantQuestionValue occupant) {

    public static Identification toModel(IdentificationDto identificationDto) {
        if(identificationDto == null) {
            return null;
        }

        return new Identification(
                identificationDto.identification(),
                identificationDto.access(),
                identificationDto.situation(),
                identificationDto.category(),
                identificationDto.occupant());
    }

    public static IdentificationDto fromModel(Identification identification) {
        return new IdentificationDto(identification.identification(),
                identification.access(),
                identification.situation(),
                identification.category(),
                identification.occupant());
    }
}
