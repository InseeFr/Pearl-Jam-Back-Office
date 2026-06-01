package fr.insee.pearljam.domain.surveyunit.service.application;

import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitFetchPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPort;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPresenter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SurveyUnitCompletedService implements SurveyUnitCompletedPort {

    private final SurveyUnitFetchPort surveyUnitFetchPort;

    @Override
    public <T> T getCompletedSurveyUnits(String campaignId, SurveyUnitCompletedPresenter<T> presenter) {

        List<StateType> stateTypes = List.of(StateType.CLO, StateType.FIN);
        List<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits = surveyUnitFetchPort.getSurveyUnitsByStatesAndCampaignId(stateTypes, campaignId);
        return presenter.present(surveyUnits);
    }
}
