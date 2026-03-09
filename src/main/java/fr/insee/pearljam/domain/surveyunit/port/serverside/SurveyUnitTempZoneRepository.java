package fr.insee.pearljam.domain.surveyunit.port.serverside;

import fr.insee.pearljam.domain.surveyunit.model.SurveyUnitTempZone;

import java.util.List;

public interface SurveyUnitTempZoneRepository {
    void deleteBySurveyUnitId(String id);

    SurveyUnitTempZone save(SurveyUnitTempZone surveyUnitTempZone);

    List<SurveyUnitTempZone> findAll();
}
