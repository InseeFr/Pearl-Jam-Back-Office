package fr.insee.pearljam.domain.surveyunit.service;

import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitHistoryPort;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitHistoryPresenter;
import fr.insee.pearljam.domain.surveyunit.port.out.SurveyUnitHistoryRepositoryPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitHistory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyUnitHistoryService implements SurveyUnitHistoryPort {

    private final SurveyUnitHistoryRepositoryPort surveyUnitHistoryRepositoryPort;

    @Override
    public <T> T getSurveyUnitHistory(String surveyUnitId, SurveyUnitHistoryPresenter<T> presenter) {
        SurveyUnitHistory surveyUnitHistory = surveyUnitHistoryRepositoryPort.findSurveyUnitHistory(surveyUnitId);
        return presenter.present(surveyUnitHistory);
    }
}
