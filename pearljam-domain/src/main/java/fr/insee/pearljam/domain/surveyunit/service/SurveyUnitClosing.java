package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitExistencePort;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.service.exception.ClosingCauseAlreadyExistsException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SurveyUnitClosing implements SurveyUnitClosingPort {

    private final ClosingCauseRepository closingCauseRepository;
    private final SurveyUnitExistencePort surveyUnitExistencePort;

    @Override
    @Transactional
    public void addClosingCauseToMultipleSurveyUnits(List<String> surveyUnitIds, ClosingCauseType type) {

        // Check all survey units exist (1 query)
        List<String> existingSurveyUnits = surveyUnitExistencePort.findExistingIds(surveyUnitIds);
        List<String> missingSurveyUnits = surveyUnitIds.stream()
                .filter(id -> !existingSurveyUnits.contains(id))
                .toList();

        if (!missingSurveyUnits.isEmpty()) {
            throw new SurveyUnitNotFoundException(
                    "Survey units not found: " + String.join(", ", missingSurveyUnits)
            );
        }

        List<String> surveyUnitsWithClosingCause =
                closingCauseRepository.findSurveyUnitIdsWithClosingCause(surveyUnitIds);

        if (!surveyUnitsWithClosingCause.isEmpty()) {
            throw new ClosingCauseAlreadyExistsException(
                    "Closing causes already exist for: " + String.join(", ", surveyUnitsWithClosingCause)
            );
        }

        closingCauseRepository.addClosingCauseToSurveyUnits(surveyUnitIds, type);
    }
}