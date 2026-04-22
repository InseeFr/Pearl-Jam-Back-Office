package fr.insee.pearljam.domain.reporting.port.in;

import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitToClose;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface SurveyUnitsToClosePort {

    List<SurveyUnitToClose> getSurveyUnitsToClose(String userId, HttpServletRequest request);
}
