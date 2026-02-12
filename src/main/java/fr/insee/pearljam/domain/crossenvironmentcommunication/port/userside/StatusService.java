package fr.insee.pearljam.domain.crossenvironmentcommunication.port.userside;

public interface StatusService {
    void updateStatus(String surveyUnit, String type);
}
