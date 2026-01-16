package fr.insee.pearljam.domain.surveyunit.port.userside;

public interface StatusService {
    void updateStatus(String surveyUnit, String type);
}