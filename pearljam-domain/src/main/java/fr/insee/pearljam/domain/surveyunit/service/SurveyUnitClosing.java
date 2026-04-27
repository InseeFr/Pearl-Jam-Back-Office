package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.model.closingcause.ClosingCauseType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitClosingPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitPort;
import fr.insee.pearljam.domain.surveyunit.port.out.ClosingCauseRepository;
import fr.insee.pearljam.domain.surveyunit.service.exception.ClosingCauseAlreadyExistsException;
import fr.insee.pearljam.domain.surveyunit.service.exception.SurveyUnitNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SurveyUnitClosing implements SurveyUnitClosingPort {

    private final ClosingCauseRepository closingCauseRepository;
    private final SurveyUnitPort surveyUnitPort;

    @Override
    @Transactional
    public void addClosingCauseToMultipleSurveyUnits(List<String> surveyUnitIds, ClosingCauseType type) {

        for(String suId : surveyUnitIds)
        {
            if(!surveyUnitPort.existsSurveyUnitById(suId))
            {
                throw new SurveyUnitNotFoundException(suId);
            }

            if(closingCauseRepository.existsClosingCauseFromSurveyUnitId(suId)) {
                throw new ClosingCauseAlreadyExistsException(suId);
            }

            closingCauseRepository.addClosingCauseToSurveyUnit(suId, type);
        }
    }
}