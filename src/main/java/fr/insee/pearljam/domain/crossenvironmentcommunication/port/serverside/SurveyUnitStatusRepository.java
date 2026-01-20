package fr.insee.pearljam.domain.crossenvironmentcommunication.port.serverside;

import fr.insee.pearljam.domain.crossenvironmentcommunication.model.SurveyUnitStatus;

public interface SurveyUnitStatusRepository {
    void save(SurveyUnitStatus status);
}
