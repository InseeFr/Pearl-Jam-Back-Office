package fr.insee.pearljam.api.web.annotation;

import fr.insee.pearljam.api.campaign.dto.input.VisibilityCampaignUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneDateCampaignVisibilityValidator implements ConstraintValidator<AtLeastOneDateValid, VisibilityCampaignUpdateDto> {
    @Override
    public boolean isValid(VisibilityCampaignUpdateDto dto, ConstraintValidatorContext context) {
        return dto.managementStartDate() != null || dto.interviewerStartDate() != null || dto.identificationPhaseStartDate() != null ||
                dto.collectionStartDate() != null || dto.collectionEndDate() != null || dto.endDate() != null;
    }
}
