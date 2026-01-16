package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.api.domain.State;
import fr.insee.pearljam.api.domain.StateType;
import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.surveyunit.port.serverside.MovedSurveyUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MovedSurveyUnitDaoAdapter implements MovedSurveyUnitRepository {
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    @Transactional
    public void updateMovedSurveyUnit(String surveyUnitId) {
        var surveyUnit = surveyUnitRepository.findById(surveyUnitId);
        surveyUnit.ifPresent(unit -> {
            unit.setPriority(true);
            var state = new State(System.currentTimeMillis(), unit, StateType.INS);
            unit.getStates().clear();
            unit.getStates().add(state);

            // Update identification to set demenagementWeb to true instead of resetting it
            unit.setIdentificationDemenagementWeb(true);

            // Remove all persons for this survey unit
            unit.getPersons().clear();

            surveyUnitRepository.save(unit);
        });
    }
}