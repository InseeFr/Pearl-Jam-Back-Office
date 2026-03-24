package fr.insee.pearljam.infrastructure.persistence.surveyunit.adapter;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitTempZoneDB;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitTempZoneRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.jpa.SurveyUnitTempZoneJpaRepository;
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
    public SurveyUnitTempZoneDB save(SurveyUnitTempZoneDB surveyUnitTempZone) {
        return surveyUnitTempZoneJpaRepository.save(surveyUnitTempZone);
    }

    @Override
    public List<SurveyUnitTempZoneDB> findAll() {
        return surveyUnitTempZoneJpaRepository.findAll();
    }
}
