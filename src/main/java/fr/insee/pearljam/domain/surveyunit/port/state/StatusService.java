package fr.insee.pearljam.domain.surveyunit.port.state;

public interface StatusService {
    void updateStatus(String surveyUnit, String type);
}
