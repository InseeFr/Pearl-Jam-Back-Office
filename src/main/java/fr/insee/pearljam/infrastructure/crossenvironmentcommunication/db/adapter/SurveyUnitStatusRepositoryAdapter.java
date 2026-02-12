package fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.adapter;

import fr.insee.pearljam.api.repository.SurveyUnitRepository;
import fr.insee.pearljam.domain.crossenvironmentcommunication.model.SurveyUnitStatus;
import fr.insee.pearljam.domain.crossenvironmentcommunication.port.serverside.SurveyUnitStatusRepository;
import fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.entity.SurveyUnitStatusEntity;
import fr.insee.pearljam.infrastructure.crossenvironmentcommunication.db.jpa.SurveyUnitStatusJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
@Slf4j
public class SurveyUnitStatusRepositoryAdapter implements SurveyUnitStatusRepository {
    private final SurveyUnitStatusJpaRepository crudRepository;
    private final SurveyUnitRepository surveyUnitRepository;

    @Override
    public void save(SurveyUnitStatus status) {
        var surveyUnit = surveyUnitRepository.findById(status.surveyUnitId());
        if (surveyUnit.isPresent()) {
            var entity = new SurveyUnitStatusEntity();
            entity.setState(status.state());
            entity.setSurveyUnit(surveyUnit.get());
            crudRepository.save(entity);
        } else {
            log.warn("SurveyUnit not found with id: {}", status.surveyUnitId());
        }
    }

}
