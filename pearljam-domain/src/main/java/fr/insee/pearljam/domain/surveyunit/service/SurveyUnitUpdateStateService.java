package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitExistencePort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitStateUpdatePort;
import fr.insee.pearljam.domain.surveyunit.port.out.StateRepository;
import fr.insee.pearljam.domain.surveyunit.service.exception.ForbiddenOperation;
import fr.insee.pearljam.domain.surveyunit.service.exception.StateNotFoundException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitUpdateStateService implements SurveyUnitStateUpdatePort {
    private final SurveyUnitExistencePort surveyUnitExistencePort;
    private final SurveyUnitClosingPort surveyUnitClosingPort;
    private final StateRepository stateRepository;

    @Override
    @Transactional
    public void addStateToMultipleSurveyUnits(List<String> surveyUnitIds, StateType state) {
        List<String> existingSurveyUnits = surveyUnitExistencePort.findExistingIds(surveyUnitIds);
        List<String> missingSurveyUnits = surveyUnitIds.stream()
                .filter(id -> !existingSurveyUnits.contains(id))
                .toList();

        if (!missingSurveyUnits.isEmpty()) {
            throw new SurveyUnitNotFoundException(String.join(", ", missingSurveyUnits));
        }

        surveyUnitIds.forEach(suId -> addStateToSurveyUnit(suId, state));
    }

    @Override
    @Transactional
    public void addStateToSurveyUnit(String surveyUnitId, StateType state) {
        Optional<StateType> surveyUnitCurrentState = stateRepository.findLastStateBySurveyUnitId(surveyUnitId);

        if(surveyUnitCurrentState.isEmpty())
        {
            throw new StateNotFoundException(surveyUnitId);
        }

        if (!StateBusinessRules.stateCanBeModifiedByManager(surveyUnitCurrentState.get(), state)) {
            throw new ForbiddenOperation(
                    String.format("Cannot pass from state %s to state %s, it does not respect business rules",
                            surveyUnitCurrentState.get(), state));
        }

        if (StateType.TBR.equals(state) || StateType.FIN.equals(state)) {
                    log.info("Deleting closing causes of survey unit {}", surveyUnitId);
                    surveyUnitClosingPort.deleteClosingCauseBySurveyUnitId(surveyUnitId);
                }

        stateRepository.saveStateForSurveyUnits(List.of(surveyUnitId), state, new Date().toInstant());
    }
}
