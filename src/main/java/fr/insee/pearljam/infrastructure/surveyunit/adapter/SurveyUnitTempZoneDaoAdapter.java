package fr.insee.pearljam.infrastructure.surveyunit.adapter;

import fr.insee.pearljam.domain.surveyunit.model.SurveyUnitTempZone;
import fr.insee.pearljam.domain.surveyunit.port.serverside.SurveyUnitTempZoneRepository;
import fr.insee.pearljam.infrastructure.surveyunit.jpa.SurveyUnitTempZoneJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SurveyUnitTempZoneDaoAdapter implements SurveyUnitTempZoneRepository {
    private final SurveyUnitTempZoneJpaRepository surveyUnitTempZoneJpaRepository;

    @Override
    public void deleteBySurveyUnitId(String id) {
        surveyUnitTempZoneJpaRepository.deleteBySurveyUnitId(id);
    }

    @Override
    public SurveyUnitTempZone save(SurveyUnitTempZone surveyUnitTempZone) {
        return surveyUnitTempZoneJpaRepository.save(surveyUnitTempZone);
    }

    @Override
    public List<SurveyUnitTempZone> findAll() {
        return surveyUnitTempZoneJpaRepository.findAll();
    }
}
