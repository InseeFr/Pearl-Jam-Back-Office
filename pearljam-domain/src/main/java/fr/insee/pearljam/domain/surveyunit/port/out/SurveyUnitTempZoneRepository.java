package fr.insee.pearljam.domain.surveyunit.port.out;

import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitTempZoneDB;

import java.util.List;

public interface SurveyUnitTempZoneRepository {
    void deleteBySurveyUnitId(String id);

    SurveyUnitTempZoneDB save(SurveyUnitTempZoneDB surveyUnitTempZone);

    List<SurveyUnitTempZoneDB> findAll();
}
