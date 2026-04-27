package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.campaign.service.dummy.SurveyUnitRepositoryStub;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitPort;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitRepository;
import fr.insee.pearljam.infrastructure.persistence.surveyunit.entity.SurveyUnitDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SurveyUnit Service Tests")
class SurveyUnitTest {
    private SurveyUnitPort surveyUnitPort;
    private SurveyUnitRepository surveyUnitRepository;

    @BeforeEach
    void setUp()
    {
        surveyUnitRepository = new SurveyUnitRepositoryStub();
        surveyUnitPort = new SurveyUnitService(surveyUnitRepository);
    }

    @Test
    @DisplayName("Should return true when a survey unit exists")
    void shouldReturnTrueIfSurveyUnitExists()
    {
        String surveyUnitId = "TEST_ID";
        surveyUnitRepository.save(buildDefaultSurveyUnit(surveyUnitId));
        assertTrue(surveyUnitPort.existsSurveyUnitById(surveyUnitId));
    }

    @Test
    @DisplayName("Should return false when a survey unit does not exist")
    void shouldReturnFalseIfSurveyDoesNotExist()
    {
        String surveyUnitId = "TEST_ID";
        assertFalse(surveyUnitPort.existsSurveyUnitById(surveyUnitId));
    }

    SurveyUnitDB buildDefaultSurveyUnit(String surveyUnitId)
    {
        return new SurveyUnitDB(surveyUnitId, true, true, null,
                null, null, null, null,null);
    }

}
