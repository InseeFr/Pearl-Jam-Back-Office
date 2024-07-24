package fr.insee.pearljam.api.web.annotation;

import fr.insee.pearljam.api.campaign.dto.input.VisibilityUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneDateVisibilityValidator implements ConstraintValidator<AtLeastOneDateValid, VisibilityUpdateDto> {
    @Override
    public boolean isValid(VisibilityUpdateDto dto, ConstraintValidatorContext context) {
        return dto.managementStartDate() != null || dto.interviewerStartDate() != null || dto.identificationPhaseStartDate() != null ||
                dto.collectionStartDate() != null || dto.collectionEndDate() != null || dto.endDate() != null;
    }
}
