package fr.insee.pearljam.domain.reporting.service;

import fr.insee.pearljam.domain.reporting.port.in.SurveyUnitsToClosePort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitService;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitToClose;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitsToCloseService implements SurveyUnitsToClosePort {

    private final SurveyUnitService surveyUnitService;

    @Override
    public List<SurveyUnitToClose> getSurveyUnitsToClose(String userId, HttpServletRequest request) {
        return surveyUnitService.getClosableSurveyUnitsForReporting(request, userId);
    }
}
