package fr.insee.pearljam.domain.surveyunit.service.application;

import fr.insee.pearljam.domain.campaign.port.in.DateService;
import fr.insee.pearljam.domain.surveyunit.model.StateType;
import fr.insee.pearljam.domain.surveyunit.port.in.SurveyUnitFetchPort;
import fr.insee.pearljam.domain.surveyunit.readmodel.SurveyUnitFetchedByStatesAndCampaignIdView;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPort;
import fr.insee.pearljam.domain.surveyunit.port.in.application.SurveyUnitCompletedPresenter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SurveyUnitCompletedService implements SurveyUnitCompletedPort {

    private final SurveyUnitFetchPort surveyUnitFetchPort;
    private final DateService dateService;

    @Override
    public <T> T getCompletedSurveyUnits(String userId, String campaignId, String search, Pageable pageable, SurveyUnitCompletedPresenter<T> presenter) {
        List<StateType> stateTypes = List.of(StateType.CLO, StateType.FIN);
        Page<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnits = surveyUnitFetchPort.getSurveyUnitsByStatesAndCampaignId(userId, stateTypes, campaignId, search, pageable);

        Instant now = Instant.ofEpochMilli(dateService.getCurrentTimestamp());

        List<SurveyUnitFetchedByStatesAndCampaignIdView> surveyUnitsCompleted =
                surveyUnits.stream()
                        .filter(s -> Instant.ofEpochMilli(Long.parseLong(s.endDate()))
                                .isBefore(now))
                        .toList();

        Page<SurveyUnitFetchedByStatesAndCampaignIdView> filteredPage =
                new PageImpl<>(
                        surveyUnitsCompleted,
                        pageable,
                        surveyUnitsCompleted.size()
                );

        return presenter.present(filteredPage);
    }
}
