package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.userside.MovedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MovedServiceImpl implements MovedService {
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    public void updateMovedSurveyUnit(String surveyUnitId) {
        var surveyUnit = surveyUnitRepository.findById(surveyUnitId);
        surveyUnit.ifPresent(unit -> {
            unit.setPriority(true);
            var state = new State(System.currentTimeMillis(), unit, StateType.INS);
            unit.setStates(Set.of(state));

            // Update identification to set demenagementWeb to true instead of resetting it
            var identification = unit.getIdentification();
            if (identification != null) {
                identification.setDemenagementWeb(true);
            }

            // Remove all persons for this survey unit
            unit.getPersons().clear();

            surveyUnitRepository.save(unit);
        });
    }
}
