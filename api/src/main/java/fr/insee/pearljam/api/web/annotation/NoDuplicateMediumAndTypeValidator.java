package fr.insee.pearljam.api.web.annotation;

import fr.insee.pearljam.api.campaign.dto.input.CommunicationTemplateCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoDuplicateMediumAndTypeValidator implements ConstraintValidator<NoDuplicateMediumAndType, List<CommunicationTemplateCreateDto>> {
    @Override
    public boolean isValid(List<CommunicationTemplateCreateDto> communicationTemplates, ConstraintValidatorContext context) {
        if(communicationTemplates == null) {
            return true;
        }

        Set<CommunicationMediumTypeKey> uniqueMediumTypePairs = new HashSet<>();
        return communicationTemplates.stream()
                .map(template -> new CommunicationMediumTypeKey(template.medium(), template.type()))
                .allMatch(uniqueMediumTypePairs::add);
    }
}
